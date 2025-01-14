package jmri.managers.configurexml;

import java.util.List;
import jmri.ConfigureManager;
import jmri.InstanceManager;
import jmri.Manager;
import jmri.SignalHead;
import jmri.SignalHeadManager;
import jmri.configurexml.ConfigXmlManager;
import jmri.configurexml.JmriConfigureXmlException;
import jmri.configurexml.XmlAdapter;
import jmri.jmrix.internal.InternalSystemConnectionMemo;
import jmri.managers.AbstractSignalHeadManager;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the abstract base and store functionality for configuring
 * SignalHeadManagers, working with AbstractSignalHeadManagers.
 * <p>
 * Typically, a subclass will just implement the load(Element turnouts) class,
 * relying on implementation here to load the individual turnouts. Note that
 * these are stored explicitly, so the resolution mechanism doesn't need to see
 * *Xml classes for each specific SignalHead or AbstractSignalHead subclass at
 * store time.
 * <p>
 * Based on AbstractTurnoutManagerConfigXML
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2003, 2008
 */
public class AbstractSignalHeadManagerXml extends AbstractNamedBeanManagerConfigXML {

    public AbstractSignalHeadManagerXml() {
    }

    /**
     * Default implementation for storing the contents of a SignalHeadManager.
     * <p>
     * Unlike most other managers, the individual SignalHead objects are stored
     * separately via the configuration system so they can have separate type
     * information.
     *
     * @param o Object to store, of type SignalHeadManager
     * @return Element containing the complete info
     */
    @Override
    @SuppressWarnings("deprecation") // needs careful unwinding for Set operations
    public Element store(Object o) {
        Element signalheads = new Element("signalheads");
        setStoreElementClass(signalheads);
        SignalHeadManager sm = (SignalHeadManager) o;
        if (sm != null) {
            java.util.Iterator<String> iter
                    = sm.getSystemNameList().iterator();

            // don't return an element if there are not signalheads to include
            if (!iter.hasNext()) {
                return null;
            }

            // store the signalheads
            while (iter.hasNext()) {
                String sname = iter.next();
                if (sname == null) {
                    log.error("System name null during store, skipped");
                    continue;
                }
                log.debug("system name is " + sname);
                SignalHead sub = sm.getBySystemName(sname);
                Element e = ConfigXmlManager.elementFromObject(sub);
                if (e != null) {
                    signalheads.addContent(e);
                }
            }
        }
        return signalheads;
    }

    /**
     * Subclass provides implementation to create the correct top element,
     * including the type information. Default implementation is to use the
     * local class here.
     *
     * @param turnouts The top-level element being created
     */
    public void setStoreElementClass(Element turnouts) {
        turnouts.setAttribute("class", this.getClass().getName());
    }

    /**
     * Create a SignalHeadManager object of the correct class, then register and
     * fill it.
     *
     * @param shared  Shared top level Element to unpack.
     * @param perNode Per-node top level Element to unpack.
     * @return true if successful
     */
    @Override
    public boolean load(Element shared, Element perNode) {
        // create the master object
        replaceSignalHeadManager();

        // load individual turnouts
        loadSignalHeads(shared, perNode);
        return true;
    }

    @Override
    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    /**
     * Utility method to load the individual SignalHead objects. If there's no
     * additional info needed for a specific signal head type, invoke this with
     * the parent of the set of SignalHead elements.
     *
     * @param shared  Element containing the SignalHead elements to load.
     * @param perNode Element containing any per-node information associated
     *                with the shared Element.
     */
    public void loadSignalHeads(Element shared, Element perNode) {
        InstanceManager.getDefault(SignalHeadManager.class);

        // load the contents
        List<Element> items = shared.getChildren();
        if (log.isDebugEnabled()) {
            log.debug("Found " + items.size() + " signal heads");
        }
        for (int i = 0; i < items.size(); i++) {
            // get the class, hence the adapter object to do loading
            Element item = items.get(i);
            String adapterName = item.getAttribute("class").getValue();
            log.debug("load via " + adapterName);
            try {
                XmlAdapter adapter = (XmlAdapter) Class.forName(adapterName).getDeclaredConstructor().newInstance();
                // and do it
                adapter.load(item, null);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException 
                        | IllegalAccessException | java.lang.reflect.InvocationTargetException
                        | JmriConfigureXmlException e) {
                log.error("Exception while loading {}: {}", item.getName(), e, e);
            }
        }
    }

    /**
     * Replace the current signal head manager, if there is one, with one newly
     * created during a load operation. This is skipped if they are of the same
     * absolute type.
     */
    protected void replaceSignalHeadManager() {
        if (InstanceManager.getDefault(SignalHeadManager.class).getClass().getName()
                .equals(AbstractSignalHeadManager.class.getName())) {
            return;
        }
        // if old manager exists, remove it from configuration process
        InstanceManager.getOptionalDefault(SignalHeadManager.class).ifPresent((shm) -> {
            InstanceManager.getDefault(ConfigureManager.class).deregister(shm);
        });

        // register new one with InstanceManager
        AbstractSignalHeadManager pManager = new AbstractSignalHeadManager(InstanceManager.getDefault(InternalSystemConnectionMemo.class));
        InstanceManager.setDefault(SignalHeadManager.class, pManager);
        // register new one for configuration
        InstanceManager.getOptionalDefault(ConfigureManager.class).ifPresent((cm) -> {
            cm.registerConfig(pManager, Manager.SIGNALHEADS);
        });
    }

    @Override
    public int loadOrder() {
        return InstanceManager.getDefault(SignalHeadManager.class).getXMLOrder();
    }

    private final static Logger log = LoggerFactory.getLogger(AbstractSignalHeadManagerXml.class);

}

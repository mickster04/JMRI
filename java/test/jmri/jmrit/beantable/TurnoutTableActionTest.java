package jmri.jmrit.beantable;

import apps.gui.GuiLafPreferencesManager;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import jmri.InstanceManager;
import jmri.Turnout;
import jmri.util.JUnitUtil;
import org.junit.*;
import org.netbeans.jemmy.operators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;

/**
 * Tests for the jmri.jmrit.beantable.TurnoutTableAction class.
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class TurnoutTableActionTest extends AbstractTableActionBase {

    @Test
    public void testCTor() {
        Assert.assertNotNull("exists", a);
    }

    @Override
    public String getTableFrameName() {
        return Bundle.getMessage("TitleTurnoutTable");
    }

    @Override
    @Test
    public void testGetClassDescription() {
        Assert.assertEquals("Turnout Table Action class description", "Turnout Table", a.getClassDescription());
    }

    /**
     * Check the return value of includeAddButton.
     * <p>
     * The table generated by this action includes an Add button.
     */
    @Override
    @Test
    public void testIncludeAddButton() {
        Assert.assertTrue("Default include Add... button", a.includeAddButton());
    }

    /**
     * Check Turnout Table GUI, menus and graphic state presentation.
     *
     * @since 4.7.4
     */
    @Test
    public void testAddAndInvoke() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        a.actionPerformed(null); // show table

        // create 2 turnouts and see if they exist
        Turnout it1 = InstanceManager.turnoutManagerInstance().provideTurnout("IT1");
        Turnout it2 = InstanceManager.turnoutManagerInstance().provideTurnout("IT2");
        it1.setCommandedState(Turnout.THROWN);
        it2.setCommandedState(Turnout.CLOSED);

        // set graphic state column display preference to false, read by createModel()
        InstanceManager.getDefault(GuiLafPreferencesManager.class).setGraphicTableState(false);

        TurnoutTableAction _tTable;
        _tTable = new TurnoutTableAction();
        Assert.assertNotNull("found TurnoutTable frame", _tTable);
        // prevent there are 2 menubars with the same name
        _tTable.dispose();

        // set to true, use icons
        InstanceManager.getDefault(GuiLafPreferencesManager.class).setGraphicTableState(true);
        TurnoutTableAction _t1Table;
        _t1Table = new TurnoutTableAction();
        Assert.assertNotNull("found TurnoutTable1 frame", _t1Table);
        JFrame t1Frame = JFrameOperator.waitJFrame(Bundle.getMessage("TitleTurnoutTable"), true, true);

        // test Add pane
        _t1Table.addPressed(null);
        JFrame af = JFrameOperator.waitJFrame(Bundle.getMessage("TitleAddTurnout"), true, true);
        Assert.assertNotNull("found Add frame", af);
        // close Add pane
        _t1Table.cancelPressed(null);
        // more Turnout Add pane tests are in TurnoutTableWindowTest

        // Open Automation pane to test Automation menu
        jmri.jmrit.turnoutoperations.TurnoutOperationFrame tof = new jmri.jmrit.turnoutoperations.TurnoutOperationFrame(null);
        // create dialog (bypassing menu)
        JDialogOperator am = new JDialogOperator("Turnout Operation Editor"); // TODO I18N using Bundle
        Assert.assertNotNull("found Automation menu dialog", am);
        // close pane
        JButtonOperator jbo = new JButtonOperator(am, "OK");
        jbo.pushNoBlock(); // instead of .push();
        am.dispose();

        // Open Speed pane to test Speed menu, which displays a JOptionPane
        log.debug("Speed pane started at " + java.time.LocalTime.now()); // debug
        JFrameOperator main = new JFrameOperator(Bundle.getMessage("TitleTurnoutTable")); 
        // Use GUI menu to open Speeds pane:
	
	//This is a modal JOptionPane, so create a thread to dismiss it.
	Thread t = new Thread(() -> {
            try {
               jmri.util.swing.JemmyUtil.confirmJOptionPane(main,Bundle.getMessage("TurnoutGlobalSpeedMessageTitle"), "", "OK");
            } catch( org.netbeans.jemmy.TimeoutExpiredException tee) {
               // we're waiting for this thread to finish in the main method,
               // so any exception here means we failed.
               log.error("caught timeout exception while waiting for modal dialog",tee);
            }
        });
        t.setName("Default Speeds Dialog Close Thread");
        t.start();
        // pushMenuNoBlock is used, because dialog is modal
        JMenuBarOperator mainbar = new JMenuBarOperator(main);
        mainbar.pushMenu(Bundle.getMessage("SpeedsMenu")); // stops at top level
        JMenuOperator jmo = new JMenuOperator(mainbar, Bundle.getMessage("SpeedsMenu"));
        JPopupMenu jpm = jmo.getPopupMenu();
        JMenuItemOperator jmio = new JMenuItemOperator(new JPopupMenuOperator(jpm),Bundle.getMessage("SpeedsMenuItemDefaults"));
        jmio.pushNoBlock();

        // wait for the dismiss thread to finish
        JUnitUtil.waitFor(()-> { return !t.isAlive(); 
                  }, "Dismiss Default Speeds Thread finished");

        // clean up
        JUnitUtil.dispose(af);
        //as.dispose(); // uncomment when test is Speeds menu activated
        JUnitUtil.dispose(tof);
        _t1Table.dispose();
        JUnitUtil.dispose(t1Frame);
    }

    @Override
    public String getAddFrameName(){
        return Bundle.getMessage("TitleAddTurnout");
    }

    @Test
    @Override
    public void testAddButton() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Assume.assumeTrue(a.includeAddButton());
        a.actionPerformed(null);
        JFrame f = JFrameOperator.waitJFrame(getTableFrameName(), true, true);

        // find the "Add... " button and press it.
	jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f), Bundle.getMessage("ButtonAdd"));
        JFrame f1 = JFrameOperator.waitJFrame(getAddFrameName(), true, true);
	jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f1), Bundle.getMessage("ButtonClose")); // not sure why this is close in this frame.
        JUnitUtil.dispose(f1);
        JUnitUtil.dispose(f);
    }

    @Test
    @Override
    public void testEditButton() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Assume.assumeTrue(a.includeAddButton());
        a.actionPerformed(null);
        JFrame f = JFrameOperator.waitJFrame(getTableFrameName(), true, true);

        // find the "Add... " button and press it.
        JFrameOperator jfo = new JFrameOperator(f);
        jmri.util.swing.JemmyUtil.pressButton(jfo,Bundle.getMessage("ButtonAdd"));
        new org.netbeans.jemmy.QueueTool().waitEmpty();
        JFrame f1 = JFrameOperator.waitJFrame(getAddFrameName(), true, true);
        //Enter 1 in the text field labeled "Hardware address:"
        JTextField hwAddressField = JTextFieldOperator.findJTextField(f1, new NameComponentChooser("hwAddressTextField"));
        Assert.assertNotNull("hwAddressTextField", hwAddressField);

        // set to "1"
        new JTextFieldOperator(hwAddressField).typeText("1");

        //and press create 
	jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f1),Bundle.getMessage("ButtonCreate"));
        new org.netbeans.jemmy.QueueTool().waitEmpty();

        JTableOperator tbl = new JTableOperator(jfo, 0);
        // find the "Edit" button and press it.  This is in the table body.
        tbl.clickOnCell(0,TurnoutTableAction.EDITCOL);
        JFrame f2 = JFrameOperator.waitJFrame(getEditFrameName(), true, true);
        jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f2), Bundle.getMessage("ButtonCancel"));
        JUnitUtil.dispose(f2);
        JUnitUtil.dispose(f1);
        JUnitUtil.dispose(f);
    }

    @Override
    public String getEditFrameName(){
        return "Edit Turnout IT1";
    }

    // The minimal setup for log4J
    @Before
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.resetProfileManager();
        jmri.util.JUnitUtil.initInternalTurnoutManager();
        jmri.util.JUnitUtil.initDefaultUserMessagePreferences();
        helpTarget = "package.jmri.jmrit.beantable.TurnoutTable"; 
        a = new TurnoutTableAction();
    }

    @After
    @Override
    public void tearDown() {
        a = null;
        JUnitUtil.tearDown();
    }

    private final static Logger log = LoggerFactory.getLogger(TurnoutTableActionTest.class);

}

package jmri.jmrix.pi;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import jmri.Sensor;
import jmri.util.JUnitUtil;
import jmri.util.junit.annotations.ToDo;
import org.junit.*;

/**
 * Tests for RaspberryPiSensorManager.
 * <p>
 * This is somehow not resetting the GPIO support, so each reference to a "pin"
 * needs to be do a different one, even across multiple test types.
 *
 * @author Paul Bender Copyright (C) 2016
 */
public class RaspberryPiSensorManagerTest extends jmri.managers.AbstractSensorMgrTestBase {

    @Override
    public String getSystemName(int i) {
        return l.getSystemPrefix() + "S" + i;
    }

    @Test
    public void ConstructorTest(){
        Assert.assertNotNull(l);
    }

    @Test
    public void checkPrefix(){
        Assert.assertEquals("Prefix", "P", l.getSystemPrefix());
    }

    @Override
    @Test
    public void testSensorPutGet() {
        // create
        Sensor t = l.newSensor(getSystemName(10), "mine");
        // check
        Assert.assertTrue("real object returned ", t != null);
        Assert.assertTrue("user name correct ", t == l.getByUserName("mine"));
        Assert.assertTrue("system name correct ", t == l.getBySystemName(getSystemName(10)));
    }

    @Override
    @Test
    public void testSingleObject() {
        // test that you always get the same representation
        Sensor t1 = l.newSensor(getSystemName(11), "mine");
        Assert.assertTrue("t1 real object returned ", t1 != null);
        Assert.assertTrue("same by user ", t1 == l.getByUserName("mine"));
        Assert.assertTrue("same by system ", t1 == l.getBySystemName(getSystemName(11)));

        Sensor t2 = l.newSensor(getSystemName(11), "mine");
        Assert.assertTrue("t2 real object returned ", t2 != null);
        // check
        Assert.assertTrue("same new ", t1 == t2);
    }

    @Override
    @Test
    public void testRename() {
        Sensor t1 = l.newSensor(getSystemName(12), "before");
        Assert.assertNotNull("t1 real object ", t1);
        t1.setUserName("after");
        Sensor t2 = l.getByUserName("after");
        Assert.assertEquals("same object", t1, t2);
        Assert.assertEquals("no old object", null, l.getByUserName("before"));
    }

    @Override
    @Test
    public void testDefaultSystemName() {
        // create
        Sensor t = l.provideSensor("" + 13);
        // check
        Assert.assertTrue("real object returned ", t != null);
        Assert.assertTrue("system name correct ", t == l.getBySystemName(getSystemName(13)));
    }

    @Override
    @Test
    public void testMoveUserName() {
        Sensor t1 = l.provideSensor("21");
        Sensor t2 = l.provideSensor("22");
        t1.setUserName("UserName");
        Assert.assertTrue(t1 == l.getByUserName("UserName"));
        
        t2.setUserName("UserName");
        Assert.assertTrue(t2 == l.getByUserName("UserName"));

        Assert.assertTrue(null == t1.getUserName());
    }

    @Override
    @Test
    public void testPullResistanceConfigurable(){
       Assert.assertTrue("Pull Resistance Configurable", l.isPullResistanceConfigurable());
    }

    @Override
    @Test
    public void testProvideName() {
        // create
        Sensor t = l.provide("" + 14);
        // check
        Assert.assertTrue("real object returned ", t != null);
        Assert.assertTrue("system name correct ", t == l.getBySystemName(getSystemName(14)));
    }

    @Test
    @Ignore("This test doesn't work for this class")
    @ToDo("RaspberryPiSensorTest.setUp throws the error: java.lang.IllegalArgumentException: This GPIO pin already exists: GPIO 1")
    @Override
    public void testRegisterDuplicateSystemName() {
    }

    @Override
    @Before
    public void setUp() {
       JUnitUtil.setUp();
       GpioProvider myprovider = new PiGpioProviderScaffold();
       GpioFactory.setDefaultProvider(myprovider);
       JUnitUtil.resetInstanceManager();
       l = new RaspberryPiSensorManager(new RaspberryPiSystemConnectionMemo());
    }

    @After
    public void tearDown() {
        JUnitUtil.clearShutDownManager();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.tearDown();
    }

}

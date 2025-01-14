package jmri.jmrix.secsi;

import jmri.Manager.NameValidity;
import jmri.util.JUnitAppender;
import jmri.util.JUnitUtil;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

/**
 * JUnit tests for the SerialAddress utility class.
 *
 * @author	Dave Duchamp Copyright 2004
 * @author Bob Jacobsen Copyright 2007
 */
public class SerialAddressTest {

    private SerialTrafficControlScaffold tcis = null;
    private SecsiSystemConnectionMemo memo = null;

    @Test
    public void testValidSystemNameFormat() {
        Assert.assertTrue("valid format - VL2", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL2", 'L', "V"));
        Assert.assertTrue("valid format - VL0B2", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL0B2", 'L', "V"));
        Assert.assertTrue("invalid format - VL", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL", 'L', "V"));

        Assert.assertTrue("invalid format - VLB2", NameValidity.VALID != SerialAddress.validSystemNameFormat("VLB2", 'L', "V"));
        //JUnitAppender.assertWarnMessage("invalid character in number field system name: VL");
        //JUnitAppender.assertWarnMessage("no node address before 'B' in system name: VLB2");

        Assert.assertTrue("valid format - VL2005", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL2005", 'L', "V"));
        Assert.assertTrue("valid format - VL2B5", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL2B5", 'L', "V"));
        Assert.assertTrue("valid format - VT2005", NameValidity.VALID == SerialAddress.validSystemNameFormat("VT2005", 'T', "V"));
        Assert.assertTrue("valid format - VT2B5", NameValidity.VALID == SerialAddress.validSystemNameFormat("VT2B5", 'T', "V"));
        Assert.assertTrue("valid format - VS2005", NameValidity.VALID == SerialAddress.validSystemNameFormat("VS2005", 'S', "V"));
        Assert.assertTrue("valid format - V3S2B5", NameValidity.VALID == SerialAddress.validSystemNameFormat("V3S2B5", 'S', "V3"));

        Assert.assertTrue("invalid format - VY2005", NameValidity.VALID != SerialAddress.validSystemNameFormat("VY2005", 'L', "V"));
        JUnitAppender.assertErrorMessage("illegal character in header field system name: VY2005");

        Assert.assertTrue("invalid format - VY2B5", NameValidity.VALID != SerialAddress.validSystemNameFormat("VY2B5", 'L', "V"));
        JUnitAppender.assertErrorMessage("illegal character in header field system name: VY2B5");

        Assert.assertTrue("valid format - VL22001", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL22001", 'L', "V"));
        Assert.assertTrue("valid format - VL22B1", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL22B1", 'L', "V"));

        Assert.assertTrue("invalid format - VL22000", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL22000", 'L', "V"));
        //JUnitAppender.assertWarnMessage("bit number not in range 1 - 999 in system name: VL22000");

        Assert.assertTrue("invalid format - VL22B0", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL22B0", 'L', "V"));
        //JUnitAppender.assertWarnMessage("bit number field out of range in system name: VL22B0");

        Assert.assertTrue("valid format - VL2999", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL2999", 'L', "V"));
        Assert.assertTrue("invalid format - VL2B2048", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL2B2048", 'L', "V"));
        //JUnitAppender.assertWarnMessage("bit number field out of range in system name: VL2B2048");

        Assert.assertTrue("invalid format - VL2B2049", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL2B2049", 'L', "V"));
        //JUnitAppender.assertWarnMessage("bit number field out of range in system name: VL2B2049");

        Assert.assertTrue("invalid format - VL2B33", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL2B33", 'L', "V"));
        //JUnitAppender.assertWarnMessage("bit number field out of range in system name: VL2B33");

        Assert.assertTrue("valid format - VL127032", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL127032", 'L', "V"));

        Assert.assertTrue("valid format - VL127001", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL127001", 'L', "V"));

        Assert.assertTrue("invalid format - VL127000", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL127000", 'L', "V"));
        //JUnitAppender.assertWarnMessage("bit number not in range 1 - 999 in system name: VL127000");

        Assert.assertTrue("valid format - VL127B7", NameValidity.VALID == SerialAddress.validSystemNameFormat("VL127B7", 'L', "V"));

        Assert.assertTrue("invalid format -VL128B7", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL128B7", 'L', "V"));
        //JUnitAppender.assertWarnMessage("node address field out of range in system name: VL128B7");

        Assert.assertTrue("invalid format - VL2oo5", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL2oo5", 'L', "V"));
        //JUnitAppender.assertWarnMessage("invalid character in number field system name: VL2oo5");

        Assert.assertTrue("invalid format - VL2aB5", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL2aB5", 'L', "V"));
        //JUnitAppender.assertWarnMessage("invalid character in node address field of system name: VL2aB5");

        Assert.assertTrue("invalid format - VL2B5x", NameValidity.VALID != SerialAddress.validSystemNameFormat("VL2B5x", 'L', "V"));
        //JUnitAppender.assertWarnMessage("invalid character in bit number field of system name: VL2B5x");
    }

    @Test
    public void testGetBitFromSystemName() {
        Assert.assertEquals("VL2", 2, SerialAddress.getBitFromSystemName("VL2", "V"));
        Assert.assertEquals("VL2002", 2, SerialAddress.getBitFromSystemName("VL2002", "V"));
        Assert.assertEquals("VL1", 1, SerialAddress.getBitFromSystemName("VL1", "V"));
        Assert.assertEquals("VL2001", 1, SerialAddress.getBitFromSystemName("VL2001", "V"));
        Assert.assertEquals("VL999", 999, SerialAddress.getBitFromSystemName("VL999", "V"));
        Assert.assertEquals("V1L2999", 999, SerialAddress.getBitFromSystemName("V1L2999", "V1"));

        Assert.assertEquals("VL29O9", 0, SerialAddress.getBitFromSystemName("VL29O9", "V"));
        JUnitAppender.assertErrorMessage("invalid character in number field of system name: VL29O9");

        Assert.assertEquals("VL0B7", 7, SerialAddress.getBitFromSystemName("VL0B7", "V"));
        Assert.assertEquals("VL2B7", 7, SerialAddress.getBitFromSystemName("VL2B7", "V"));
        Assert.assertEquals("VL0B1", 1, SerialAddress.getBitFromSystemName("VL0B1", "V"));
        Assert.assertEquals("VL2B1", 1, SerialAddress.getBitFromSystemName("VL2B1", "V"));
        Assert.assertEquals("VL0B2048", 2048, SerialAddress.getBitFromSystemName("VL0B2048", "V"));
        Assert.assertEquals("VL11B2048", 2048, SerialAddress.getBitFromSystemName("VL11B2048", "V"));
    }

    @Test
    public void testGetNodeFromSystemName() {
        SerialNode d = new SerialNode(14, SerialNode.DAUGHTER, tcis);
        SerialNode c = new SerialNode(17, SerialNode.DAUGHTER, tcis);
        SerialNode b = new SerialNode(127, SerialNode.DAUGHTER, tcis);
        Assert.assertEquals("node of VL14007", d, SerialAddress.getNodeFromSystemName("VL14007", tcis));
        Assert.assertEquals("node of VL14B7", d, SerialAddress.getNodeFromSystemName("VL14B7", tcis));
        Assert.assertEquals("node of VL127007", b, SerialAddress.getNodeFromSystemName("VL127007", tcis));
        Assert.assertEquals("node of VL127B7", b, SerialAddress.getNodeFromSystemName("VL127B7", tcis));
        Assert.assertEquals("node of VL17007", c, SerialAddress.getNodeFromSystemName("VL17007", tcis));
        Assert.assertEquals("node of VL17B7", c, SerialAddress.getNodeFromSystemName("VL17B7", tcis));
        Assert.assertEquals("node of VL11007", null, SerialAddress.getNodeFromSystemName("VL11007", tcis));
        Assert.assertEquals("node of VL11B7", null, SerialAddress.getNodeFromSystemName("VL11B7", tcis));
    }

    @Test
    public void testValidSystemNameConfig() {
        SerialNode d = new SerialNode(4, SerialNode.DAUGHTER, tcis);
        SerialNode c = new SerialNode(10, SerialNode.DAUGHTER, tcis);
        Assert.assertNotNull("exists", d);
        Assert.assertNotNull("exists", c);
        Assert.assertTrue("valid config VL4007", SerialAddress.validSystemNameConfig("VL4007", 'L', tcis));
        Assert.assertTrue("valid config VL4B7", SerialAddress.validSystemNameConfig("VL4B7", 'L', tcis));
        Assert.assertTrue("valid config VS10007", SerialAddress.validSystemNameConfig("VS10007", 'S', tcis));
        Assert.assertTrue("valid config VS10B7", SerialAddress.validSystemNameConfig("VS10B7", 'S', tcis));
        Assert.assertTrue("valid config VL10032", SerialAddress.validSystemNameConfig("VL10032", 'L', tcis));
        Assert.assertTrue("valid config VL10B32", SerialAddress.validSystemNameConfig("VL10B32", 'L', tcis));

        Assert.assertTrue("invalid config VL10033", !SerialAddress.validSystemNameConfig("VL10033", 'L', tcis));
        JUnitAppender.assertWarnMessage("VL10033 invalid; bad bit number");

        Assert.assertTrue("invalid config VL10B33", !SerialAddress.validSystemNameConfig("VL10B33", 'L', tcis));
        //JUnitAppender.assertWarnMessage("bit number field out of range in system name: VL10B33");
        JUnitAppender.assertWarnMessage("VL10B33 invalid; bad format");

        Assert.assertTrue("valid config VS10016", SerialAddress.validSystemNameConfig("VS10016", 'S', tcis));
        Assert.assertTrue("valid config VS10B16", SerialAddress.validSystemNameConfig("VS10B16", 'S', tcis));

        Assert.assertTrue("invalid config VS10017", !SerialAddress.validSystemNameConfig("VS10017", 'S', tcis));
        JUnitAppender.assertWarnMessage("VS10017 invalid; bad bit number");

        Assert.assertTrue("invalid config VS10B17", !SerialAddress.validSystemNameConfig("VS10B17", 'S', tcis));
        JUnitAppender.assertWarnMessage("VS10B17 invalid; bad bit number");

        Assert.assertTrue("valid config VT4016", SerialAddress.validSystemNameConfig("VT4016", 'T', tcis));
        Assert.assertTrue("valid config VT4B16", SerialAddress.validSystemNameConfig("VT4B16", 'T', tcis));

        Assert.assertTrue("invalid config VT4117", !SerialAddress.validSystemNameConfig("VT4117", 'T', tcis));
        JUnitAppender.assertWarnMessage("VT4117 invalid; bad bit number");

        Assert.assertTrue("invalid config VT4B117", !SerialAddress.validSystemNameConfig("VT4B117", 'T', tcis));
        //JUnitAppender.assertWarnMessage("bit number field out of range in system name: VT4B117");
        JUnitAppender.assertWarnMessage("VT4B117 invalid; bad format");

        Assert.assertTrue("valid config VS4008", SerialAddress.validSystemNameConfig("VS4008", 'S', tcis));
        Assert.assertTrue("valid config VS4B8", SerialAddress.validSystemNameConfig("VS4B8", 'S', tcis));

        Assert.assertTrue("invalid config VS4017", !SerialAddress.validSystemNameConfig("VS4017", 'S', tcis));
        JUnitAppender.assertWarnMessage("VS4017 invalid; bad bit number");

        Assert.assertTrue("invalid config VS4B19", !SerialAddress.validSystemNameConfig("VS4B19", 'S', tcis));
        JUnitAppender.assertWarnMessage("VS4B19 invalid; bad bit number");

        Assert.assertTrue("invalid config VL11007", !SerialAddress.validSystemNameConfig("VL11007", 'L', tcis));
        JUnitAppender.assertWarnMessage("VL11007 invalid; no such node");

        Assert.assertTrue("invalid config VL11B7", !SerialAddress.validSystemNameConfig("VL11B7", 'L', tcis));
        JUnitAppender.assertWarnMessage("VL11B7 invalid; no such node");

    }

    @Test
    public void testConvertSystemNameFormat() {
        Assert.assertEquals("convert VL14007", "VL14B7", SerialAddress.convertSystemNameToAlternate("VL14007", "V"));
        Assert.assertEquals("convert VS7", "VS0B7", SerialAddress.convertSystemNameToAlternate("VS7", "V"));
        Assert.assertEquals("convert VT4007", "VT4B7", SerialAddress.convertSystemNameToAlternate("VT4007", "V"));
        Assert.assertEquals("convert VL14B7", "VL14007", SerialAddress.convertSystemNameToAlternate("VL14B7", "V"));
        Assert.assertEquals("convert VL0B7", "VL7", SerialAddress.convertSystemNameToAlternate("VL0B7", "V"));
        Assert.assertEquals("convert VS4B7", "VS4007", SerialAddress.convertSystemNameToAlternate("VS4B7", "V"));
        Assert.assertEquals("convert VL14B8", "VL14008", SerialAddress.convertSystemNameToAlternate("VL14B8", "V"));

        Assert.assertEquals("convert VL128B7", "", SerialAddress.convertSystemNameToAlternate("VL128B7", "V"));
        //JUnitAppender.assertWarnMessage("node address field out of range in system name: VL128B7");
    }

    @Test
    public void testNormalizeSystemName() {
        Assert.assertEquals("normalize VL14007", "VL14007", SerialAddress.normalizeSystemName("VL14007", "V"));
        Assert.assertEquals("normalize V2L007", "V2L7", SerialAddress.normalizeSystemName("V2L007", "V2"));
        Assert.assertEquals("normalize VL004007", "VL4007", SerialAddress.normalizeSystemName("VL004007", "V"));
        Assert.assertEquals("normalize VL14B7", "VL14B7", SerialAddress.normalizeSystemName("VL14B7", "V"));
        Assert.assertEquals("normalize VL0B7", "VL0B7", SerialAddress.normalizeSystemName("VL0B7", "V"));
        Assert.assertEquals("normalize VL004B7", "VL4B7", SerialAddress.normalizeSystemName("VL004B7", "V"));
        Assert.assertEquals("normalize VL014B0008", "VL14B8", SerialAddress.normalizeSystemName("VL014B0008", "V"));

        Assert.assertEquals("normalize VL128B7", "", SerialAddress.normalizeSystemName("VL128B7", "V"));
        //JUnitAppender.assertWarnMessage("node address field out of range in system name: VL128B7");
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();

        memo = new SecsiSystemConnectionMemo(); // needed to create tcis
        tcis = new SerialTrafficControlScaffold(memo);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}

package fi.vm.kapa.rova.logging;

import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class LoggerTest {

    @Test
    public void testMaskHetuEnding1() {
        String logString = "121212A123A";
        assertEquals(Logger.maskHetuEnding(logString), "121212AXXXX");
    } 

    @Test
    public void testMaskHetuEnding2() {
        String logString = "121212-123A asdf";
        assertEquals(Logger.maskHetuEnding(logString), "121212-XXXX asdf");
    } 

    @Test
    public void testMaskHetuEnding3() {
        String logString = "asdf 121212+123A";
        assertEquals(Logger.maskHetuEnding(logString), "asdf 121212+XXXX");
    } 

    @Test
    public void testMaskHetuEnding4() {
        String logString = "121212A123ABC";
        assertEquals(Logger.maskHetuEnding(logString), "121212A123ABC");
    }

    @Test
    public void testFormatted() {
        org.slf4j.Logger loggerMock = EasyMock.createMock(org.slf4j.Logger.class);
        expect(loggerMock.isDebugEnabled()).andReturn(true);
        loggerMock.debug("Hello, world!");
        expectLastCall().once();
        replay(loggerMock);
        Logger logger = Logger.getLogger(LoggerTest.class);
        logger.setSlf4jLogger(loggerMock);
        logger.debug("Hello, %s!", "world");
    }

    @Test
    public void testLogMap() {
        org.slf4j.Logger loggerMock = EasyMock.createMock(org.slf4j.Logger.class);
        expect(loggerMock.isDebugEnabled()).andReturn(true).times(2);
        loggerMock.debug("{\"key\":\"value\"}");
        expectLastCall().once();
        replay(loggerMock);
        Logger logger = Logger.getLogger(LoggerTest.class);
        logger.setSlf4jLogger(loggerMock);
        logger.debugMap()
                .set("key", "value")
                .log();
    }

    @Test
    public void testLogMapHetu() {
        org.slf4j.Logger loggerMock = EasyMock.createMock(org.slf4j.Logger.class);
        expect(loggerMock.isDebugEnabled()).andReturn(true).times(2);
        loggerMock.debug("{\"hetu\":\"190436-XXXX\"}");
        expectLastCall().once();
        replay(loggerMock);
        Logger logger = Logger.getLogger(LoggerTest.class);
        logger.setSlf4jLogger(loggerMock);
        logger.debugMap()
                .set("hetu", "190436-7510")
                .log();
    }

}


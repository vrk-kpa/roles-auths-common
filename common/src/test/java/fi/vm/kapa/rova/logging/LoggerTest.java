package fi.vm.kapa.rova.logging;

import org.easymock.EasyMock;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class LoggerTest {


    @Test
    public void testFormatted() {
        org.slf4j.Logger loggerMock = EasyMock.createMock(org.slf4j.Logger.class);
        expect(loggerMock.isDebugEnabled()).andReturn(true).times(2);
        loggerMock.debug("{\"" + Logger.Field.MSG + "\":\"Hello, world!\"}");
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

}


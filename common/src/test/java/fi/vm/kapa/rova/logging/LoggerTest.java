/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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


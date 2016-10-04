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
package fi.vm.kapa.rova.rest.validation;

import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.utils.EncodingUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;

import static fi.vm.kapa.rova.utils.EncodingUtils.*;
import static org.junit.Assert.*;

/**
 * Created by Juha Korkalainen on 23.2.2016.
 */
public class ValidationUtilTest {

    private static final String TEST_KEY = "testkey";
    private static final String PREFIX = "/rest";
    private static final String URL_INFO_PATH = "service";
    private static final String PATH_PARAM = "123456+9999";
    private static final String BASE_URI = "http://example.com" + PREFIX + "/";
    private static final String URL = "http://example.com" + PREFIX + "/" + URL_INFO_PATH;
    private static final String PARAMS = "?param1=test&param2=testtest";
    private static final String URL_WITH_PARAMS = URL + PARAMS;

    private static final Logger LOG = Logger.getLogger(ValidationUtilTest.class);

    @Test
    public void testHandleClientRequestContext() throws Exception {
        ClientRequestContext clientRequestContext = EasyMock.createMock(ClientRequestContext.class);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap();
        URI uri = new URI(URL);

        EasyMock.expect(clientRequestContext.getHeaders()).andReturn(headers).once();
        EasyMock.expect(clientRequestContext.getUri()).andReturn(uri).once();
        EasyMock.replay(clientRequestContext);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);
        assertTrue(vUtil.handleClientRequestContext(clientRequestContext));
        assertEquals(2, headers.size());

        String hash = (headers.get(ValidationUtil.HASH_HEADER_NAME).get(0)).toString();
        assertNotNull(hash);
        String timestamp = (headers.get(ValidationUtil.TIMESTAMP_HEADER_NAME).get(0)).toString();
        assertNotNull(timestamp);

        Assert.assertEquals(hash, HashGenerator.hash(uri.getPath() + timestamp, TEST_KEY));
        EasyMock.verify(clientRequestContext);
    }

    @Test
    public void testHandleClientRequestContextWithParams() throws Exception {
        ClientRequestContext clientRequestContext = EasyMock.createMock(ClientRequestContext.class);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap();
        URI uri = new URI(URL_WITH_PARAMS);

        EasyMock.expect(clientRequestContext.getHeaders()).andReturn(headers).once();
        EasyMock.expect(clientRequestContext.getUri()).andReturn(uri).once();
        EasyMock.replay(clientRequestContext);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);
        assertTrue(vUtil.handleClientRequestContext(clientRequestContext));
        assertEquals(2, headers.size());

        String hash = (headers.get(ValidationUtil.HASH_HEADER_NAME).get(0)).toString();
        assertNotNull(hash);
        String timestamp = (headers.get(ValidationUtil.TIMESTAMP_HEADER_NAME).get(0)).toString();
        assertNotNull(timestamp);

        Assert.assertEquals(hash, HashGenerator.hash(uri.getPath()+ "?"+ uri.getQuery() + timestamp, TEST_KEY));
        EasyMock.verify(clientRequestContext);
    }

    @Test
    public void testHandleContainerRequestContext() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);
        UriInfo uInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uInfo.getRequestUri()).andReturn(new URI(URL)).once();
        EasyMock.expect(uInfo.getBaseUri()).andReturn(new URI(BASE_URI)).once();
        EasyMock.replay(uInfo);

        String timestamp = "" + System.currentTimeMillis();
        String hash = getHash(timestamp);

        EasyMock.expect(crc.getHeaderString(ValidationUtil.HASH_HEADER_NAME)).andReturn(hash).once();
        EasyMock.expect(crc.getHeaderString(ValidationUtil.TIMESTAMP_HEADER_NAME)).andReturn(timestamp).once();
        EasyMock.expect(crc.getUriInfo()).andReturn(uInfo).once();
        EasyMock.replay(crc);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);

        assertTrue(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(uInfo);
        EasyMock.verify(crc);
    }

    @Test
    public void testHandleContainerRequestContextWithParams() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);
        UriInfo uInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uInfo.getRequestUri()).andReturn(new URI(URL_WITH_PARAMS)).once();
        EasyMock.expect(uInfo.getBaseUri()).andReturn(new URI(BASE_URI)).once();
        EasyMock.replay(uInfo);

        String timestamp = "" + System.currentTimeMillis();
        String hash = getHash(timestamp, false, true);

        EasyMock.expect(crc.getHeaderString(ValidationUtil.HASH_HEADER_NAME)).andReturn(hash).once();
        EasyMock.expect(crc.getHeaderString(ValidationUtil.TIMESTAMP_HEADER_NAME)).andReturn(timestamp).once();
        EasyMock.expect(crc.getUriInfo()).andReturn(uInfo).once();
        EasyMock.replay(crc);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);

        assertTrue(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(uInfo);
        EasyMock.verify(crc);
    }

    @Test
    public void testHandleContainerRequestContextWithPathParam() throws Exception {
        LOG.info("*** TESTING WITH PATH PARAM");
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);
        UriInfo uInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uInfo.getRequestUri()).andReturn(new URI(URL + "/" + encodePathParam(PATH_PARAM))).once();
        EasyMock.expect(uInfo.getBaseUri()).andReturn(new URI(BASE_URI)).once();
        EasyMock.replay(uInfo);

        String timestamp = "" + System.currentTimeMillis();
        String hash = getHash(timestamp, true, false);

        EasyMock.expect(crc.getHeaderString(ValidationUtil.HASH_HEADER_NAME)).andReturn(hash).once();
        EasyMock.expect(crc.getHeaderString(ValidationUtil.TIMESTAMP_HEADER_NAME)).andReturn(timestamp).once();
        EasyMock.expect(crc.getUriInfo()).andReturn(uInfo).once();
        EasyMock.replay(crc);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);

        assertTrue(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(uInfo);
        EasyMock.verify(crc);
    }

    @Test
    public void testHandleContainerRequestContextNoTimestamp() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);

        EasyMock.expect(crc.getHeaderString(ValidationUtil.TIMESTAMP_HEADER_NAME)).andReturn(null).once();
        EasyMock.replay(crc);
        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);
        assertFalse(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(crc);
    }

    @Test
    public void testHandleContainerRequestContextNoHash() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);

        EasyMock.expect(crc.getHeaderString(ValidationUtil.TIMESTAMP_HEADER_NAME)).andReturn("" + System.currentTimeMillis()).once();
        EasyMock.expect(crc.getHeaderString(ValidationUtil.HASH_HEADER_NAME)).andReturn(null).once();
        EasyMock.replay(crc);
        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);
        assertFalse(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(crc);
    }


    @Test
    public void testHandleContainerRequestContextInvalidHash() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);
        UriInfo uInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uInfo.getRequestUri()).andReturn(new URI(URL)).once();
        EasyMock.expect(uInfo.getBaseUri()).andReturn(new URI(BASE_URI)).once();
        EasyMock.replay(uInfo);

        String timestamp = "" + System.currentTimeMillis();
        String hash = "INVALIDHASH";

        EasyMock.expect(crc.getHeaderString(ValidationUtil.HASH_HEADER_NAME)).andReturn(hash).once();
        EasyMock.expect(crc.getHeaderString(ValidationUtil.TIMESTAMP_HEADER_NAME)).andReturn(timestamp).once();
        EasyMock.expect(crc.getUriInfo()).andReturn(uInfo).once();
        EasyMock.replay(crc);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);

        assertFalse(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(uInfo);
        EasyMock.verify(crc);
    }

    @Test
    public void testOldRequestHandleContainerRequestContext() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);
        long timestamp = System.currentTimeMillis() - 5000;

        EasyMock.expect(crc.getHeaderString(ValidationUtil.TIMESTAMP_HEADER_NAME)).andReturn("" + timestamp).once();
        EasyMock.expect(crc.getHeaderString(ValidationUtil.HASH_HEADER_NAME)).andReturn("ANYHASH").once();
        EasyMock.replay(crc);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);
        assertFalse(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(crc);
    }

    private String getHash(String timestamp) throws IOException {
        return getHash(timestamp, false, false);
    }

    private String getHash(String timestamp, boolean includePathParam, boolean includeParams) throws IOException {
        StringBuilder data = new StringBuilder();
        data.append(PREFIX);
        data.append("/");
        data.append(URL_INFO_PATH);
        if (includePathParam) {
            data.append("/");
            data.append(PATH_PARAM);
        }
        if (includeParams) {
            data.append(PARAMS);
        }
        data.append(timestamp);

        return HashGenerator.hash(data.toString(), TEST_KEY);
    }

}
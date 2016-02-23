package fi.vm.kapa.rova.rest.validation;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.constraints.AssertTrue;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Juha Korkalainen on 23.2.2016.
 */
public class ValidationUtilTest {

    private static final String TEST_KEY = "testkey";
    private static final String PREFIX = "/rest";
    private static final String URL_INFO_PATH = "service";
    private static final String URL = "http://example.com" + PREFIX + "/" + URL_INFO_PATH;

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

        String hash = (((List<Object>) headers.get(ValidationUtil.HASH_HEADER_NAME)).get(0)).toString();
        assertNotNull(hash);
        String timestamp = (((List<Object>) headers.get(ValidationUtil.TIMESTAMP_HEADER_NAME)).get(0)).toString();
        assertNotNull(timestamp);

        Assert.assertEquals(hash, HashGenerator.hash(uri.getPath() + timestamp, TEST_KEY));
        EasyMock.verify(clientRequestContext);
    }

    @Test
    public void testHandleContainerRequestContext() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);
        UriInfo uInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uInfo.getPath()).andReturn(URL_INFO_PATH).once();
        EasyMock.replay(uInfo);

        URI uri = new URI(URL);
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
    public void testHandleContainerRequestContextInvalidHash() throws Exception {
        ContainerRequestContext crc = EasyMock.createMock(ContainerRequestContext.class);
        UriInfo uInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uInfo.getPath()).andReturn(URL_INFO_PATH).once();
        EasyMock.replay(uInfo);

        URI uri = new URI(URL);
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
        EasyMock.replay(crc);

        ValidationUtil vUtil = new ValidationUtil(TEST_KEY, 2, PREFIX);
        assertFalse(vUtil.handleContainerRequestContext(crc));
        EasyMock.verify(crc);
    }


    private String getHash(String timestamp) throws IOException {
        return HashGenerator.hash(PREFIX + "/" + URL_INFO_PATH + timestamp, TEST_KEY);
    }
}
package fi.vm.kapa.rova.rest.validation;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;

import fi.vm.kapa.rova.logging.Logger;

public class ValidationUtil {

    public final static String HASH_HEADER_NAME = "X-RoVa-Hash";
    public final static String TIMESTAMP_HEADER_NAME = "X-RoVa-timestamp";

    public static Logger LOG = Logger.getLogger(ValidationUtil.class);
    
    private String apiKey;
    private long requestAliveMillis;
    private String pathPrefix; // url prefix, for client side

    public ValidationUtil(String apiKey, int requestAliveSeconds, String pathPrefix) {
        this.apiKey = apiKey;
        this.requestAliveMillis = requestAliveSeconds * 1000;
        this.pathPrefix = pathPrefix == null ? "" : pathPrefix;
    }

    /**
     * Handle out bound client request
     *
     * @param context
     * @return
     */
    public boolean handleClientRequestContext(ClientRequestContext context) throws IOException {
        MultivaluedMap<String, Object> headers = context.getHeaders();
        String timestamp = "" + System.currentTimeMillis();
        String data = context.getUri().getPath();
        data = data + timestamp;
        String hash = HashGenerator.hash(data, apiKey);
        headers.putSingle(HASH_HEADER_NAME, hash);
        headers.putSingle(TIMESTAMP_HEADER_NAME, timestamp);
        return true;
    }

    /**
     * Handle in bound client request
     *
     * @param context
     * @return
     */
    public boolean handleContainerRequestContext(ContainerRequestContext context) throws IOException {
        String timestamp = context.getHeaderString(TIMESTAMP_HEADER_NAME);
        if (requestAlive(timestamp)) {
            String data = pathPrefix + "/" + context.getUriInfo().getPath() + timestamp;
            String hash = context.getHeaderString(HASH_HEADER_NAME);
            return matches(hash, data, apiKey);
        } else {
            LOG.info("Request timestamp (%s) was older than %d", timestamp, requestAliveMillis);
            return false;
        }
    }
    

    private boolean requestAlive(String timestampHeader) {
        long timestamp = Long.parseLong(timestampHeader);
        return (System.currentTimeMillis() < (timestamp + requestAliveMillis));
    }

    private boolean matches(String hash, String data, String apiKey) throws IOException {
        return hash.equals(HashGenerator.hash(data, apiKey));
    }


}

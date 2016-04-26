package fi.vm.kapa.rova.rest.validation;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

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
        URI uri = context.getUri();
        StringBuilder data = new StringBuilder();
        data.append(uri.getPath());
        String query = uri.getQuery();
        if (query != null && query.length() > 0) {
            data .append("?");
            data.append(query);
        }
        data.append(timestamp);
        String hash = HashGenerator.hash(data.toString(), apiKey);
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
        if (timestamp == null) {
            LOG.info("Found request without proper timestamp header: " + TIMESTAMP_HEADER_NAME);
            return false;
        }

        String hash = context.getHeaderString(HASH_HEADER_NAME);
        if (hash == null) {
            LOG.info("Found request without proper hash header: " + HASH_HEADER_NAME);
            return false;
        }

        if (requestAlive(timestamp)) {
            String path = getPathWithParams(context.getUriInfo());
            String data = pathPrefix + "/" + path + timestamp;
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

    private String getPathWithParams(UriInfo uInfo) {
        String uInfoPath = uInfo.getPath();
        String absolutePath = uInfo.getAbsolutePath().toString();
        String requestUri = uInfo.getRequestUri().toString();
        String path = requestUri.substring(absolutePath.length() - uInfoPath.length());
        return path;
    }

}

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.kapa.rova.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;


public class ValidationUtil {

    public static final String HASH_HEADER_NAME = "X-RoVa-Hash";
    public static final String TIMESTAMP_HEADER_NAME = "X-RoVa-timestamp";

    private static final Logger LOG = Logger.getLogger(ValidationUtil.class);
    
    private String apiKey;
    private long requestAliveMillis;
    private String pathPrefix; // url prefix, for client side

    public ValidationUtil(String apiKey, int requestAliveSeconds, String pathPrefix) {
        this.apiKey = apiKey;
        this.requestAliveMillis = requestAliveSeconds * 1000;
        this.pathPrefix = pathPrefix == null ? "" : pathPrefix;
    }
    
    public ValidationUtil(String apiKey, int requestAliveSeconds) {
        this(apiKey, requestAliveSeconds, null);
    }

    /**
     * Append validation headers to an outbound client request
     *
     * @param context Current ClientRequestContext
     */
    public void appendValidationHeaders(ClientRequestContext context) throws IOException {
        long timestamp = System.currentTimeMillis();
        String hashData = buildValidationHashData(context, timestamp);
        
        String hash = HashGenerator.hash(hashData, apiKey);
        MultivaluedMap<String, Object> headers = context.getHeaders();
        headers.putSingle(HASH_HEADER_NAME, hash);
        headers.putSingle(TIMESTAMP_HEADER_NAME, timestamp);
    }


    /**
     * Append valdation headers to outgoing HttpRequest instance
     * Assumes that given body byte[] is a String
     * @param request
     * @param body
     * @throws IOException
     */
    public void appendValidationHeaders(HttpRequest request, byte[] body) throws IOException {
        long timestamp = System.currentTimeMillis();
        String hashData = buildValidationHashData(request, body, timestamp);
        String hash = HashGenerator.hash(hashData, apiKey);
        HttpHeaders headers = request.getHeaders();
        headers.add(HASH_HEADER_NAME, hash);
        headers.add(TIMESTAMP_HEADER_NAME, ""+timestamp);
    }

    private String buildValidationHashData(HttpRequest request, byte[] body, long timestamp) {
        StringBuilder data = new StringBuilder();
        data.append(request.getURI().getPath());
        String query = request.getURI().getQuery();
        if (!StringUtils.isBlank(query) ) {
            data.append("?");
            data.append(query);
        }
        data.append(timestamp);

        if (body != null) {
            data.append(new String(body));
        }
        return data.toString();
    }


    private String buildValidationHashData(ClientRequestContext context, long timestamp) throws JsonProcessingException {
        StringBuilder data = new StringBuilder();
        data.append(context.getUri().getPath());
        String query = context.getUri().getQuery();
        if (!StringUtils.isBlank(query) ) {
            data.append("?");
            data.append(query);
        }
        data.append(timestamp);
        if (context.getEntity() != null) {
            data.append(new ObjectMapper().writeValueAsString(context.getEntity()));
        }
        return data.toString();
    }

    /**
     * Handle in bound client request
     *
     * @param context
     * @return
     */
    public boolean checkValidationHeaders(ContainerRequestContext context) throws IOException {
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

        if (!requestAlive(timestamp)) {
            throw new IOException("Request timestamp ("+timestamp+") was older than "+requestAliveMillis + " ms");
        }

        byte[] entity = null;
        if (context.getEntityStream() != null) {
            entity = StreamUtils.copyToByteArray(context.getEntityStream());
            context.setEntityStream(new ByteArrayInputStream(entity));
        }
        String path = getPathWithParams(context.getUriInfo());
        String data = pathPrefix + "/" + path + timestamp + (entity != null ? new String(entity, "UTF-8") : "");
        return matches(hash, data, apiKey);
    }

    public boolean checkValidationHeaders(HttpServletRequest request) throws IOException {
        String timestamp = request.getHeader(TIMESTAMP_HEADER_NAME);
        if (timestamp == null) {
            throw new IOException("Found request without proper timestamp header: " + TIMESTAMP_HEADER_NAME);
        }

        String hash = request.getHeader(HASH_HEADER_NAME);
        if (hash == null) {
            LOG.info("Found request without proper hash header: " + HASH_HEADER_NAME);
            throw new IOException("Found request without proper hash header: " + HASH_HEADER_NAME);
        }

        if (!requestAlive(timestamp)) {
            throw new IOException("Request timestamp ("+timestamp+") was older than "+requestAliveMillis + " ms");
        }
        byte[] entity = null;
        if (request.getMethod().matches("POST|PUT|DELETE")) {
            entity = StreamUtils.copyToByteArray(request.getInputStream());
        }

        String path = getPathWithParams(request);
        String data = pathPrefix  + path + timestamp + (entity != null ? new String(entity, "UTF-8") : "");
        return matches(hash, data, apiKey);
    }


    private boolean requestAlive(String timestampHeader) {
        long timestamp = Long.parseLong(timestampHeader);
        return (System.currentTimeMillis() < (timestamp + requestAliveMillis));
    }

    private boolean matches(String hash, String data, String apiKey) throws IOException {
        return hash.equals(HashGenerator.hash(data, apiKey));
    }

    private String getPathWithParams(UriInfo uInfo) throws UnsupportedEncodingException {
        String requestUri = (URLDecoder.decode(uInfo.getRequestUri().toString(), Charset.defaultCharset().toString()));
        String path = requestUri.substring(uInfo.getBaseUri().toString().length());
        return path;
    }

    private String getPathWithParams(HttpServletRequest request) {
        String queryString = (request.getQueryString() != null && request.getQueryString().length() > 0) ? "?" + request.getQueryString() : "";
        return request.getRequestURI() + queryString ;
    }

}

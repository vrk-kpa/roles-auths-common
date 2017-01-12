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
package fi.vm.kapa.rova.rest.identification;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * A ClientRequestFilter that appends the current request and end user identifiers
 * to outgoing REST calls.
 */
@Provider
@PreMatching
public class RequestIdentificationFilter implements ClientRequestFilter {

    public static final String ORIG_REQUEST_IDENTIFIER = "X-request-id";
    public static final String ORIG_END_USER = "X-orig-userId";

    public enum HeaderTrust {
        TRUST_REQUEST_HEADERS,
        DONT_TRUST_REQUEST_HEADERS
    };

    private String requestId;
    private String endUserId;
    private boolean allowValuesFromRequestHeaders;

    /**
     * Creates a new RequestIdentificationFilter. Reads the request and end user identification from
     * request attributes or headers.
     *
     * @param trustHeaders Whether the identification data in current HTTP request headers should be trusted.
     *                     Set to DONT_TRUST_REQUEST_HEADERS if the request originates from a browser.
     */
    public RequestIdentificationFilter(HeaderTrust trustHeaders) {
        this(null, null, trustHeaders);
    }

    /**
     * Creates a new RequestIdentificationFilter. Reads the request and end user identification from
     * request attributes or headers. The provided identification data overrides request attributes, but not
     * request headers (if header trust is set to TRUST_REQUEST_HEADERS).
     *
     * @param requestId Current request identifier.
     * @param endUserId Current end user identifier.
     * @param trustHeaders Whether the identification data in current HTTP request headers should be trusted.
     *                     Set to DONT_TRUST_REQUEST_HEADERS if the request originates from a browser.
     */
    public RequestIdentificationFilter(String requestId, String endUserId, HeaderTrust trustHeaders) {
        this.requestId = requestId;
        this.endUserId = endUserId;
        this.allowValuesFromRequestHeaders = (trustHeaders == HeaderTrust.TRUST_REQUEST_HEADERS);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        filter(ORIG_REQUEST_IDENTIFIER, requestContext, requestId);
        filter(ORIG_END_USER, requestContext, endUserId);
    }

    private void filter(String headerName, ClientRequestContext requestContext, String newValue) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String value = (String) attrs.getAttribute(headerName,
                    RequestAttributes.SCOPE_REQUEST);
            if (newValue != null) {
                value = newValue;
            }
            if (value == null && allowValuesFromRequestHeaders) {
                HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs)
                        .getRequest();
                value = httpRequest.getHeader(headerName);

            }
            if (value != null) {
                replaceHeaderValue(headerName, requestContext, value);
            }
        }
    }

    private void replaceHeaderValue(String headerName,
            ClientRequestContext requestContext, String value) {
        requestContext.getHeaders().remove(headerName);
        requestContext.getHeaders().add(headerName, value);
    }
}
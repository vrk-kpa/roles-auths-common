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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by jkorkala on 03/03/2017.
 */
public class RequestIdentificationInterceptor implements ClientHttpRequestInterceptor {

    public static final String ORIG_REQUEST_IDENTIFIER = "X-request-id";
    public static final String ORIG_END_USER = "X-orig-userId";

    public enum HeaderTrust {
        TRUST_REQUEST_HEADERS,
        DONT_TRUST_REQUEST_HEADERS
    }

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
    public RequestIdentificationInterceptor(RequestIdentificationInterceptor.HeaderTrust trustHeaders) {
        this(null, null, trustHeaders);
    }

    /**
     * Creates a new RequestIdentificationFilter. Reads the request and end user identification from
     * request attributes or headers. The provided identification data overrides request attributes, but not
     * request headers (if header trust is set to TRUST_REQUEST_HEADERS).
     *
     * @param requestId    Current request identifier.
     * @param endUserId    Current end user identifier.
     * @param trustHeaders Whether the identification data in current HTTP request headers should be trusted.
     *                     Set to DONT_TRUST_REQUEST_HEADERS if the request originates from a browser.
     */
    public RequestIdentificationInterceptor(String requestId, String endUserId, RequestIdentificationInterceptor.HeaderTrust trustHeaders) {
        this.requestId = requestId;
        this.endUserId = endUserId;
        this.allowValuesFromRequestHeaders = (trustHeaders == RequestIdentificationInterceptor.HeaderTrust.TRUST_REQUEST_HEADERS);
    }


    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        intercept(ORIG_REQUEST_IDENTIFIER, request, requestId);
        intercept(ORIG_END_USER, request, endUserId);
        return execution.execute(request, body);
    }

    private void intercept(String headerName, HttpRequest request, String newValue) {
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
                replaceHeaderValue(headerName, request.getHeaders(), value);
            }
        }
    }

    private void replaceHeaderValue(String headerName,
                                    HttpHeaders headers, String value) {
        headers.remove(headerName);
        headers.add(headerName, value);
    }

}

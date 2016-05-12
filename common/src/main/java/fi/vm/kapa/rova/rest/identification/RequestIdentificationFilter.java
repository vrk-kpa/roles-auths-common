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

@Provider
@PreMatching
public class RequestIdentificationFilter implements ClientRequestFilter {

    public static final String ORIG_REQUEST_IDENTIFIER = "X-request-id";
    public static final String ORIG_END_USER = "X-orig-userId";

    private String requestId;
    private String endUserId;
    
    public RequestIdentificationFilter() {}
    
    public RequestIdentificationFilter(String requestId, String endUserId) {
        this.requestId = requestId;
        this.endUserId = endUserId;
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
            if (value == null) {
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
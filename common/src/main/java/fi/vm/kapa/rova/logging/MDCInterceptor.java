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

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

import fi.vm.kapa.rova.utils.RequestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

public class MDCInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {

        storeRequestId(request);
        return execution.execute(request, body);
    }

    public void storeRequestId(HttpRequest request) {
        String requestId = RequestUtils.fetchRequestId();
        if (requestId == null) {
            requestId = RequestUtils.createNewRequestId();
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                attrs.setAttribute(REQUEST_ID.toString(), requestId, RequestAttributes.SCOPE_REQUEST);
                replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), requestId);
            } else {
                replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), RequestUtils.NO_REQUEST_ID);
            }
        } else {
            replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), requestId);
        }
    }

    private void replaceHeaderValue(String headerName,
            HttpHeaders headers, String value) {
        headers.remove(headerName);
        headers.add(headerName, value);
    }
}

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

import static fi.vm.kapa.rova.logging.Logger.Field.CLIENT_IP;
import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

import fi.vm.kapa.rova.utils.RemoteAddressResolver;
import fi.vm.kapa.rova.utils.RequestUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MDCFilter extends RequestUtils implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String reqId = fetchRequestId();
        boolean reqIdFound = true;
        if (reqId == null) {
            reqIdFound = false;
            reqId = createNewRequestId();
            servletRequest.setAttribute(REQUEST_ID.toString(), reqId);
        }

        MDC.put(REQUEST_ID.toString(), reqId);
        MDC.put(CLIENT_IP.toString(), RemoteAddressResolver.resolve((HttpServletRequest)servletRequest));

        try {
            if (reqIdFound) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                CustomHttpServletRequest request = new CustomHttpServletRequest((HttpServletRequest)servletRequest);
                request.addHeader(REQUEST_ID.toString(), reqId);
                filterChain.doFilter(request, servletResponse);
            }
        } finally {
            MDC.remove(REQUEST_ID.toString());
            MDC.remove(CLIENT_IP.toString());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // NOP
    }

    @Override
    public void destroy() {
        // NOP
    }

    private class CustomHttpServletRequest extends HttpServletRequestWrapper {

        private Map<String, String> customHeaderMap = null;

        public CustomHttpServletRequest(HttpServletRequest request) {
            super(request);
            customHeaderMap = new HashMap<>();
        }

        public void addHeader(String name, String value) {
            customHeaderMap.put(name, value);
        }

        @Override
        public String getParameter(String name) {
            String paramValue = super.getParameter(name);
            if (paramValue == null) {
                paramValue = customHeaderMap.get(name);
            }
            return paramValue;
        }
    }
}

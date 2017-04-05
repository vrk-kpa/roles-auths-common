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

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.*;

import java.io.IOException;
import java.util.UUID;

public class RequestInfoToContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String endUser = getEndUser();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        requestAttributes.setAttribute(RequestIdentificationInterceptor.ORIG_END_USER, endUser, RequestAttributes.SCOPE_REQUEST);

        requestAttributes.setAttribute(RequestIdentificationInterceptor.ORIG_REQUEST_IDENTIFIER,
                UUID.randomUUID().toString(), RequestAttributes.SCOPE_REQUEST);

        chain.doFilter(request, response);
    }

    protected String getEndUser() {
        if (SecurityContextHolder.getContext() != null //
                && SecurityContextHolder.getContext().getAuthentication() != null //
                && SecurityContextHolder.getContext().getAuthentication().getDetails() != null//
                && SecurityContextHolder.getContext().getAuthentication().getDetails() instanceof User) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
            return user.getUsername();
        } else {
            return null;
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // nothing to do
    }

    @Override
    public void destroy() {
        // nothing to do
    }

}

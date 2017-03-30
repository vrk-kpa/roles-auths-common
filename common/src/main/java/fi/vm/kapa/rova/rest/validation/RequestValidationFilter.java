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

import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jkorkala on 22/12/2016.
 */
public class RequestValidationFilter implements Filter {


    private ValidationUtil validationUtil;

    public RequestValidationFilter(String apiKey, int requestAliveSeconds, String pathPrefix) {
        validationUtil = new ValidationUtil(apiKey, requestAliveSeconds, pathPrefix);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        ServletRequest bufferedRequest = new BufferingHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        if (validationUtil.checkValidationHeaders((HttpServletRequest) bufferedRequest)) {
            filterChain.doFilter(bufferedRequest, servletResponse);
            return;
        }
        ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    public void destroy() {

    }

    private class BufferingHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private byte[] bytes;

        public BufferingHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            bytes = org.apache.commons.io.IOUtils.toByteArray(request.getInputStream());
        }

        public ServletInputStream getInputStream() throws IOException {

            return new ServletInputStream() {
                private InputStream is = new ByteArrayInputStream(bytes);

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return isReady();
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }

                @Override
                public int read() throws IOException {
                    return is.read();
                }
            };
        }

    }

}

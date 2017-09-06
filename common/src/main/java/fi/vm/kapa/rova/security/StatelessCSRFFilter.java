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
package fi.vm.kapa.rova.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public abstract class StatelessCSRFFilter extends OncePerRequestFilter {

    private static final String CSRF_TOKEN = "ROVA-CSRF-TOKEN";
    private static final String X_CSRF_HEADER_TOKEN = "X-CSRF-TOKEN";
    private static final String X_CSRF_URI_TOKEN = "xcsrf";
    private static final Pattern ALLOWED_METHODS = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
    private String shouldNotFilterUrl = null;

    protected abstract void onAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
    
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (requestMatches(request)) {
            final String csrfTokenValue = getCsrfTokenFromHeaderOrUri(request);
            final Cookie[] cookies = request.getCookies();

            String csrfCookieValue = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(CSRF_TOKEN)) {
                        csrfCookieValue = cookie.getValue();
                        break;
                    }
                }
            }

            if (csrfTokenValue == null || !csrfTokenValue.equals(csrfCookieValue)) {
                onAccessDenied(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
    
    public String getShouldNotFilterUrl() {
        return shouldNotFilterUrl;
    }

    public void setShouldNotFilterUrl(String shouldNotFilterUrl) {
        this.shouldNotFilterUrl = shouldNotFilterUrl;
    }

    String getCsrfTokenFromHeaderOrUri(HttpServletRequest request) {
        String csrfTokenValue = request.getHeader(X_CSRF_HEADER_TOKEN);
        if (csrfTokenValue == null) {
            csrfTokenValue = request.getParameter(X_CSRF_URI_TOKEN);
        }
        return csrfTokenValue;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        return shouldNotFilterUrl != null && request.getRequestURI().matches(shouldNotFilterUrl);
    }
    
    private boolean requestMatches(HttpServletRequest request) {
        return !ALLOWED_METHODS.matcher(request.getMethod()).matches();
    }
}

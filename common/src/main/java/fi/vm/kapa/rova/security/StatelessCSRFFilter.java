package fi.vm.kapa.rova.security;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public abstract class StatelessCSRFFilter extends OncePerRequestFilter {

    private static final String CSRF_TOKEN = "CSRF-TOKEN";
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

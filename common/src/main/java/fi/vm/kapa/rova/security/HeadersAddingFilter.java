package fi.vm.kapa.rova.security;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HeadersAddingFilter implements Filter {

    private final Predicate<HttpServletRequest> matcher;
    private final Map<String, String> headers;

    public HeadersAddingFilter(Predicate<HttpServletRequest> matcher, Map<String, String> headers) {
        this.matcher = matcher;
        this.headers = headers;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if(matcher.test(httpRequest)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            for(String key: headers.keySet()) {
                httpResponse.setHeader(key, headers.get(key));
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}

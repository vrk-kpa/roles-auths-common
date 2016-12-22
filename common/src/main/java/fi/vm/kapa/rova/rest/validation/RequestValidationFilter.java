package fi.vm.kapa.rova.rest.validation;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest bufferedRequest = new BufferingHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        validationUtil.checkValidationHeaders((HttpServletRequest) bufferedRequest);
        filterChain.doFilter(bufferedRequest, servletResponse);
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

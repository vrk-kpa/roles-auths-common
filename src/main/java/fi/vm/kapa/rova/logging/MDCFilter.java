package fi.vm.kapa.rova.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Random;

import static fi.vm.kapa.rova.logging.Logger.REQUEST_ID;

@Component
public class MDCFilter implements Filter {

    private static Logger log = Logger.getLogger(MDCFilter.class);
    private Random random;

    private static final String ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // new ReqID is randomized from these chars
    public static final String NO_REQUEST_ID = "no_request"; // will be shown as ReqID if logging outside request scope

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String reqId = fetchRequestId();
        MDC.put(REQUEST_ID, reqId);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }
    public String fetchRequestId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String requestId = (String) attrs.getAttribute(REQUEST_ID, RequestAttributes.SCOPE_REQUEST);
            if (requestId == null) {
                HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs).getRequest();
                requestId = httpRequest.getHeader(REQUEST_ID);

                if (requestId == null) {
                    StringBuilder sb = new StringBuilder(15);
                    for (int i = 0; i < 15; i++) {
                        sb.append(ALPHANUMERICS.charAt(random.nextInt(ALPHANUMERICS.length())));
                    }
                    requestId = sb.toString();
                }
                attrs.setAttribute(REQUEST_ID, requestId, RequestAttributes.SCOPE_REQUEST);
            }

            return requestId;
        } else {
            return NO_REQUEST_ID;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public void destroy() {
        // NOP
    }
}

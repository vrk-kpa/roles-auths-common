package fi.vm.kapa.rova.logging;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Provider
@PreMatching
public class LoggingClientRequestFilter implements ClientRequestFilter {

    // adds a requestId to http headers of an outgoing request
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String requestId = (String) attrs.getAttribute(Logger.REQUEST_ID, RequestAttributes.SCOPE_REQUEST);
            if (requestId != null) {
                requestContext.getHeaders().remove(Logger.REQUEST_ID);
                requestContext.getHeaders().add(Logger.REQUEST_ID, requestId);
            } else {
                HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs).getRequest();
                requestId = httpRequest.getHeader(Logger.REQUEST_ID);
                if (requestId != null) {
                    requestContext.getHeaders().remove(Logger.REQUEST_ID);
                    requestContext.getHeaders().add(Logger.REQUEST_ID, requestId);
                }
            }
        }
    }
}

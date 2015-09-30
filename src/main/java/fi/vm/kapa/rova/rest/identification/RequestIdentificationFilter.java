package fi.vm.kapa.rova.rest.identification;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class RequestIdentificationFilter implements ClientRequestFilter {

    public static final String XROAD_REQUEST_IDENTIFIER = "X-XRoad-request-id";
    public static final String XROAD_END_USER = "X-XRoad-orig-userId";

    private String requestId;
    private String endUserId;
    
    public RequestIdentificationFilter() {}
    
    public RequestIdentificationFilter(String requestId, String endUserId) {
        this.requestId = requestId;
        this.endUserId = endUserId;
    }
    
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        filter(XROAD_REQUEST_IDENTIFIER, requestContext, requestId);
        filter(XROAD_END_USER, requestContext, endUserId);
    }

    private void filter(String headerName, ClientRequestContext requestContext, String newValue) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String value = (String) attrs.getAttribute(headerName,
                    RequestAttributes.SCOPE_REQUEST);
            if (newValue != null) {
                value = newValue;
            }
            if (value == null) {
                HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs)
                        .getRequest();
                value = httpRequest.getHeader(headerName);

            }
            if (value != null) {
                replaceHeaderValue(headerName, requestContext, value);
            }
        }
    }

    private void replaceHeaderValue(String headerName,
            ClientRequestContext requestContext, String value) {
        requestContext.getHeaders().remove(headerName);
        requestContext.getHeaders().add(headerName, value);
    }

}
package fi.vm.kapa.rova.logging;

import static fi.vm.kapa.rova.logging.Logger.REQUEST_ID;

import fi.vm.kapa.rova.utils.RemoteAddressResolver;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Random;

public class MDCInterceptor implements ClientHttpRequestInterceptor {
    private Random random;

    private static final String ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // new ReqID is randomized from these chars
    public static final String NO_REQUEST_ID = "no_request"; // will be shown as ReqID if logging outside request scope

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {

        System.out.println("MDCInterceptor.intercept started");
        String reqId = fetchRequestId();
        MDC.put(REQUEST_ID, reqId);
        MDC.put(Logger.Field.CLIENT_IP.toString(), RemoteAddressResolver.resolve((HttpServletRequest)request));
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
        } finally {
            MDC.remove(REQUEST_ID);
            MDC.remove(Logger.Field.CLIENT_IP.toString());
        }
        return response;
	}

    public String fetchRequestId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String requestId = (String) attrs.getAttribute(REQUEST_ID, RequestAttributes.SCOPE_SESSION);
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
                attrs.setAttribute(REQUEST_ID, requestId, RequestAttributes.SCOPE_SESSION);
                System.out.println("NO ReqID found, generated new: "+ requestId);
            } else {
                System.out.println("FOUND ReqID: "+ requestId);
            }

            return requestId;
        } else {
            return NO_REQUEST_ID;
        }
    }
}

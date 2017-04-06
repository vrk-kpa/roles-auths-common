package fi.vm.kapa.rova.logging;

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Random;

public class MDCInterceptor implements ClientHttpRequestInterceptor {
    private Random random;

    private static final String ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // new ReqID is randomized from these chars
    public static final String NO_REQUEST_ID = "no_request"; // will be shown as ReqID if logging outside request scope

    public MDCInterceptor() {
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {

        Result result = fetchAndStoreRequestId(request);
        if (!result.foundFromRequest) {
            MDC.put(REQUEST_ID.toString(), result.requestId);
        }
        return execution.execute(request, body);
    }

    public Result fetchAndStoreRequestId(HttpRequest request) {
        Result result = new Result();
        result.foundFromRequest = true;

        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String requestId = (String) attrs.getAttribute(REQUEST_ID.toString(), RequestAttributes.SCOPE_REQUEST);
            if (requestId == null) {
                HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs).getRequest();
                requestId = httpRequest.getHeader(REQUEST_ID.toString());

                if (requestId == null) {
                    StringBuilder sb = new StringBuilder(15);
                    for (int i = 0; i < 15; i++) {
                        sb.append(ALPHANUMERICS.charAt(random.nextInt(ALPHANUMERICS.length())));
                    }
                    requestId = sb.toString();
                    result.foundFromRequest = false;
                }

                attrs.setAttribute(REQUEST_ID.toString(), requestId, RequestAttributes.SCOPE_REQUEST);
            }

            replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), requestId);
            result.requestId = requestId;
        } else {
            replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), NO_REQUEST_ID);
            result.requestId = NO_REQUEST_ID;
        }

        return result;
    }

    private void replaceHeaderValue(String headerName,
            HttpHeaders headers, String value) {
        headers.remove(headerName);
        headers.add(headerName, value);
    }
    
    class Result {
        String requestId;
        boolean foundFromRequest;
    }
}

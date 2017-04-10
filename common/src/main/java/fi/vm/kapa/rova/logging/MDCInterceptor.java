package fi.vm.kapa.rova.logging;

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

import fi.vm.kapa.rova.utils.RequestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

public class MDCInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {

        storeRequestId(request);
        return execution.execute(request, body);
    }

    public void storeRequestId(HttpRequest request) {
        String requestId = RequestUtils.fetchRequestId();
        if (requestId == null) {
            requestId = RequestUtils.createNewRequestId();
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                attrs.setAttribute(REQUEST_ID.toString(), requestId, RequestAttributes.SCOPE_REQUEST);
                replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), requestId);
            } else {
                replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), RequestUtils.NO_REQUEST_ID);
            }
        } else {
            replaceHeaderValue(REQUEST_ID.toString(), request.getHeaders(), requestId);
        }
    }

    private void replaceHeaderValue(String headerName,
            HttpHeaders headers, String value) {
        headers.remove(headerName);
        headers.add(headerName, value);
    }
}

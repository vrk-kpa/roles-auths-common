package fi.vm.kapa.rova.rest.identification;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class EndUserHeaderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = (auth != null) ? auth.getName() : null;
        request.getHeaders().remove(RequestIdentificationInterceptor.ORIG_END_USER);
        request.getHeaders().add(RequestIdentificationInterceptor.ORIG_END_USER, userName);
        return execution.execute(request, body);
    }

}

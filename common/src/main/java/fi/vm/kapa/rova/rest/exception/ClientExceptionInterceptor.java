package fi.vm.kapa.rova.rest.exception;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by jkorkala on 22/03/2017.
 */
public class ClientExceptionInterceptor implements ClientHttpRequestInterceptor {


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode().value() >= 400) {
            // todo parse error message
            throw new IOException("Got error response");
        }

        return response;

    }
}

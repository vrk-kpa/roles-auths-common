/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.rest.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.utils.RequestUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by jkorkala on 22/03/2017.
 */
public class ClientExceptionInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = Logger.getLogger(ClientExceptionInterceptor.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode().value() >= 400) {
            LOG.info("Received error response: " + response.getRawStatusCode() + " (" + response.getStatusText()
                    + ") for request: " + RequestUtils.fetchRequestId());
            if (responseHasError(response)) {
                Error error;
                try {
                    error = objectMapper.readValue(response.getBody(), Error.class);
                } catch (IOException e) {
                    LOG.info("Could not parse error message from message with status=" + response.getStatusCode());
                    return response;
                }
                throw new HttpStatusException("Got error response", error);
            }
        }

        return response;
    }

    private boolean responseHasError(ClientHttpResponse response) {
        boolean hasError = false;
        List<String> values = response.getHeaders().get(Error.ERROR_INCLUDED_HEADER_NAME);
        if (values != null) {
            hasError = values.stream().anyMatch(value -> Boolean.parseBoolean(value));
        }
        return hasError;
    }

}

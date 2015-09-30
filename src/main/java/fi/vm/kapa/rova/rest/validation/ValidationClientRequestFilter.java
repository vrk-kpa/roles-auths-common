package fi.vm.kapa.rova.rest.validation;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class ValidationClientRequestFilter implements ClientRequestFilter {

    private ValidationUtil validationUtil;

    public ValidationClientRequestFilter(String apiKey, int requestAliveSeconds, String pathPrefix) {
        validationUtil = new ValidationUtil(apiKey, requestAliveSeconds, pathPrefix);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (!validationUtil.handleClientRequestContext(requestContext)) {
            throw new IOException("unable to handle request security");
        }
    }

}

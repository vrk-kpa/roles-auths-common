package fi.vm.kapa.rova.rest.validation;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.List;

public class DummyValidationContainerRequestFilter implements ContainerRequestFilter {

    private ValidationUtil validationUtil;

    public DummyValidationContainerRequestFilter(String apiKey, int requestAliveSeconds, String pathPrefix) {
        validationUtil = new ValidationUtil(apiKey, requestAliveSeconds, pathPrefix);
    }

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        List<String> noValidate = requestContext.getUriInfo().getQueryParameters().get("noValidate");
        if ((noValidate == null || !Boolean.parseBoolean(noValidate.get(0))) &&
                !validationUtil.handleContainerRequestContext(requestContext)) {
            throw new IOException("Request validation failed (hash).");
        }
    }

}

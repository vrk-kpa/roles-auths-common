package fi.vm.kapa.rova.rest.exception;

import fi.vm.kapa.rova.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper extends AbstractExceptionMapper<WebApplicationException> {
    private static final Logger LOG = Logger.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException e) {
        LOG.error("WebApplicationException: ", e);
        return getResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
    }

}

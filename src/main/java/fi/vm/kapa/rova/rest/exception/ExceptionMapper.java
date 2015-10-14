package fi.vm.kapa.rova.rest.exception;

import fi.vm.kapa.rova.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper extends AbstractExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        LOG.error("Unhandled Exception: ", e);
        return getResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
    }

}

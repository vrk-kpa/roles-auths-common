package fi.vm.kapa.rova.rest.exception;

import fi.vm.kapa.rova.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        LOG.error("Unhandled Exception: ", e);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ExceptionType.OTHER_EXCEPTION)
                .build();
    }
}

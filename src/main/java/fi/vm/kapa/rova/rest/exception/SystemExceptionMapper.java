package fi.vm.kapa.rova.rest.exception;


import fi.vm.kapa.rova.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SystemExceptionMapper implements ExceptionMapper<SystemException> {

    private static final Logger LOG = Logger.getLogger(SystemExceptionMapper.class);

    @Override
    public Response toResponse(SystemException e) {
        LOG.error("SystemException: " + e.toString());
        LOG.error("SystemException: ", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e.getCodeNumber())
                .build();
    }
}

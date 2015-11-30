package fi.vm.kapa.rova.rest.exception;

import fi.vm.kapa.rova.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.kapa.rova.logging.Logger.REQUEST_ID;

@Provider
public class SystemExceptionMapper extends AbstractExceptionMapper<SystemException> {
    private static final Logger LOG = Logger.getLogger(SystemExceptionMapper.class);

    @Override
    public Response toResponse(SystemException e) {
        LOG.error("SystemException: " + e.toString());
        return getResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
    }

    @Override
    protected Map<String, Object> buildEntity(SystemException e) {
        HashMap<String, Object> entity = new HashMap<>(2);
        entity.put(REQUEST_ID, fetchRequestId());
        entity.put("errorMessage", e.getMessage());
        entity.put("errorCode", e.getCodeNumber());
        return entity;
    }

}

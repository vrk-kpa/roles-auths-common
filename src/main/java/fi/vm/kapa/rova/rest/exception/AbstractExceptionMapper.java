package fi.vm.kapa.rova.rest.exception;

import fi.vm.kapa.rova.logging.MDCFilter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.kapa.rova.logging.Logger.REQUEST_ID;

public abstract class AbstractExceptionMapper<T extends Throwable> implements javax.ws.rs.ext.ExceptionMapper<T> {

    public abstract Response toResponse(T e);

    public Response getResponse(Response.Status status, T e) {
        return Response.status(status)
                .entity(buildEntity(e))
                .build();
    }

    protected Map<String, Object> buildEntity(T e) {
        HashMap<String, Object> entity = new HashMap<>(3);
        entity.put(REQUEST_ID, fetchRequestId());
        entity.put("errorMessage", e.getMessage());
        entity.put("errorCode", ExceptionType.OTHER_EXCEPTION);
        return entity;
    }

    protected String fetchRequestId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return (String) attrs.getAttribute(REQUEST_ID, RequestAttributes.SCOPE_REQUEST);
        } else {
            return MDCFilter.NO_REQUEST_ID;
        }
    }
}

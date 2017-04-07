package fi.vm.kapa.rova.utils;

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

import fi.vm.kapa.rova.logging.MDCFilter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {
    public static String fetchRequestId() {
        String requestId = null; 
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            requestId = (String) attrs.getAttribute(REQUEST_ID.toString(), RequestAttributes.SCOPE_REQUEST);
            if (requestId == null) {
                HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs).getRequest();
                requestId = httpRequest.getHeader(REQUEST_ID.toString());
            }
        }
        requestId = StringUtils.isBlank(requestId) ? MDCFilter.NO_REQUEST_ID : requestId;

        return requestId;
    }

}

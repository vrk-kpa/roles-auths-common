package fi.vm.kapa.rova.utils;

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.Random;

public class RequestUtils {
    private Random random;

    private final String ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // new ReqID is randomized from these chars

    public static final String NO_REQUEST_ID = "no_request"; // will be shown as ReqID if logging outside request scope

    public RequestUtils() {
        random = new Random(System.currentTimeMillis());
    }

    public String fetchRequestId() {
        String requestId = null; 
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            requestId = (String) attrs.getAttribute(REQUEST_ID.toString(), RequestAttributes.SCOPE_REQUEST);
            if (requestId == null) {
                HttpServletRequest httpRequest = ((ServletRequestAttributes) attrs).getRequest();
                requestId = httpRequest.getHeader(REQUEST_ID.toString());
            }
        }

        return requestId;

    }

    public String createNewRequestId() {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 15; i++) {
            sb.append(ALPHANUMERICS.charAt(random.nextInt(ALPHANUMERICS.length())));
        }
        return sb.toString();
    }

}

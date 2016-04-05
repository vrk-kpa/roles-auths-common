package fi.vm.kapa.rova.security;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

public class NoCacheFilter extends HeadersAddingFilter {

    private static final Map<String, String> HEADERS = new HashMap<>();
    static {
        HEADERS.put("Cache-control", "no-cache, no-store, max-age=0, must-revalidate");
        HEADERS.put("Pragma", "no-cache");
        HEADERS.put("Expires", "0");
    }
    
    public NoCacheFilter(Predicate<HttpServletRequest> matcher) {
        super(matcher, HEADERS);
    }
    
}

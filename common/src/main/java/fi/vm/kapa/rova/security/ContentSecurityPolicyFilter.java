package fi.vm.kapa.rova.security;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

public class ContentSecurityPolicyFilter  extends HeadersAddingFilter {

    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<>();
    static {
        DEFAULT_HEADERS.put("Content-Security-Policy", "default-src 'self'; style-src 'self' 'unsafe-inline';");
    }

    public ContentSecurityPolicyFilter(Predicate<HttpServletRequest> matcher, Map<String, String> headers) {
        super(matcher, headers);
    }

    public ContentSecurityPolicyFilter(Predicate<HttpServletRequest> matcher) {
        super(matcher, DEFAULT_HEADERS);
    }
}

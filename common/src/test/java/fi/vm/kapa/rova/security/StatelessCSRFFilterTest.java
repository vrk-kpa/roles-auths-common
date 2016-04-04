package fi.vm.kapa.rova.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class StatelessCSRFFilterTest {
    
    @Test
    @Ignore
    public void testUriParsing() {
        StatelessCSRFFilter filter = new StatelessCSRFFilter() {
            @Override
            protected void onAccessDenied(HttpServletRequest request, HttpServletResponse response)
                    throws IOException, ServletException {
                //do nothing
            }
        };
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        request.setQueryString("&xcsrf=foo");
        String value = filter.getCsrfTokenFromHeaderOrUri(request);
        Assert.assertEquals("foo", value);
        
        request.setQueryString("&xcsrf=foo&something-else=asdasdasd");
        value = filter.getCsrfTokenFromHeaderOrUri(request);
        Assert.assertEquals("foo", value);
        
        request.setQueryString("&unrelated-param=asasdsa&xcsrf=foo&something-else=asdasdasd");
        value = filter.getCsrfTokenFromHeaderOrUri(request);
        Assert.assertEquals("foo", value);
        
        request.setQueryString("&nothing-here=foo&something-else=asdasdasd");
        value = filter.getCsrfTokenFromHeaderOrUri(request);
        Assert.assertNull( value);
    }

}

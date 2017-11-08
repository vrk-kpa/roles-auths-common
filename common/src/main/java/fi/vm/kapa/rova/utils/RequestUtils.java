/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.utils;

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.concurrent.ThreadLocalRandom;

public class RequestUtils {

    private static final String ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // new ReqID is randomized from these chars

    public static final String NO_REQUEST_ID = "no_request"; // will be shown as ReqID if logging outside request scope

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

        return requestId;

    }

    public static String createNewRequestId() {
        StringBuilder sb = new StringBuilder(15);
        ThreadLocalRandom randomizer = ThreadLocalRandom.current();
        for (int i = 0; i < 15; i++) {
            sb.append(ALPHANUMERICS.charAt(randomizer.nextInt(ALPHANUMERICS.length())));
        }
        return sb.toString();
    }

}

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
package fi.vm.kapa.rova.rest.exception;

import com.oracle.javafx.jmx.json.JSONException;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.logging.MDCFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.kapa.rova.logging.Logger.REQUEST_ID;

/**
 * Created by jkorkala on 03/03/2017.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = Logger.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> genericException(Exception e, WebRequest request) {
        LOG.error("Unhandled Exception: ", e);
        return handleExceptionInternal(e, buildEntity(e), getJsonHeader(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(JSONException.class)
    protected ResponseEntity<Object> jsonProcessingException(JSONException e, WebRequest request) {
        LOG.error("Unhandled JSONException: ", e);
        return handleExceptionInternal(e, buildEntity(e), getJsonHeader(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(SystemException.class)
    protected ResponseEntity<Object> systemException(SystemException e, WebRequest request) {
        LOG.error("SystemException: " + e.toString());
        return handleExceptionInternal(e, buildEntity(e, e.getCodeNumber()), getJsonHeader(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(WebApplicationException.class)
    protected ResponseEntity<Object> webApplicationException(WebApplicationException e, WebRequest request) {
        LOG.error("SystemException: " + e.toString());
        return handleExceptionInternal(e, buildEntity(e), getJsonHeader(), HttpStatus.valueOf(e.getResponse().getStatus()), request);
    }

    private HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Map<String, Object> buildEntity(Throwable e) {
        Map<String, Object> entity = new HashMap<>(3);
        entity.put(REQUEST_ID, fetchRequestId());
        entity.put("errorMessage", e.getMessage());
        entity.put("errorCode", ExceptionType.OTHER_EXCEPTION.getCodeNumber());
        return entity;
    }

    private Map<String, Object> buildEntity(Throwable e, int errorCode) {
        Map<String, Object> entity = new HashMap<>(3);
        entity.put(REQUEST_ID, fetchRequestId());
        entity.put("errorMessage", e.getMessage());
        entity.put("errorCode", errorCode);
        return entity;
    }

    private String fetchRequestId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return (String) attrs.getAttribute(REQUEST_ID, RequestAttributes.SCOPE_REQUEST);
        } else {
            return MDCFilter.NO_REQUEST_ID;
        }
    }
}

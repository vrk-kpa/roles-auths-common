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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.logging.MDCFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

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

    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<Object> jsonProcessingException(JsonProcessingException e, WebRequest request) {
        LOG.error("Unhandled JSONException: ", e);
        return handleExceptionInternal(e, buildEntity(new JsonMappingException("HTTP 400 Invalid JSON data")), getJsonHeader(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(SystemException.class)
    protected ResponseEntity<Object> systemException(SystemException e, WebRequest request) {
        LOG.error("SystemException: " + e.toString());
        return handleExceptionInternal(e, buildEntity(e, e.getCodeNumber()), getJsonHeader(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(WebApplicationException.class)
    protected ResponseEntity<Object> webApplicationException(WebApplicationException e, WebRequest request) {
        if (e.getStatus() == 204) {
            LOG.error("No content: " + e.toString());
            return handleExceptionInternal(e, null, null, HttpStatus.valueOf(204), request);
        }
        if (e.getStatus() >= 400) {
            LOG.error("WebApplicationException: " + e.toString());
        }
        return handleExceptionInternal(e, buildEntity(e), getJsonHeader(), HttpStatus.valueOf(e.getStatus()), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> accessDeniedException(AccessDeniedException e, WebRequest request) {
        LOG.error("AccessDenied: " + e.toString());
        return handleExceptionInternal(e, buildEntity(e), getJsonHeader(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> nullPointerException(NullPointerException e, WebRequest request) {
        LOG.error("NullPointerException: " + e.toString());
        return handleExceptionInternal(e, buildEntity(e), getJsonHeader(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Error.ERROR_INCLUDED_HEADER_NAME, "true");
        return headers;
    }

    private Error buildEntity(Throwable e) {
        return buildEntity(e, ExceptionType.OTHER_EXCEPTION.getCodeNumber());
    }

    private Error buildEntity(Throwable e, int errorCode) {
        Error error = new Error();
        error.setReqID(fetchRequestId());
        error.setErrorMessage(e.getMessage());
        error.setErrorCode(errorCode);
        return error;
    }

    private String fetchRequestId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return (String) attrs.getAttribute(REQUEST_ID.toString(), RequestAttributes.SCOPE_REQUEST);
        } else {
            return MDCFilter.NO_REQUEST_ID;
        }
    }
}

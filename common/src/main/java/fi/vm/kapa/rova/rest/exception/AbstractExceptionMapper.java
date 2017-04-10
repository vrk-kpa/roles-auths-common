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

import fi.vm.kapa.rova.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.kapa.rova.logging.Logger.Field.REQUEST_ID;

public abstract class AbstractExceptionMapper<T extends Throwable> extends RequestUtils implements org.glassfish.jersey.spi.ExtendedExceptionMapper<T> {

    @Override
    public abstract Response toResponse(T e);

    @Context
    protected HttpServletRequest req;

    protected Response getResponse(int status, T e) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(buildEntity(e))
                .build();
    }

    protected Map<String, Object> buildEntity(T e) {
        Map<String, Object> entity = new HashMap<>(3);
        entity.put(REQUEST_ID.toString(), fetchRequestId());
        entity.put("errorMessage", e.getMessage());
        entity.put("errorCode", ExceptionType.OTHER_EXCEPTION.getCodeNumber());
        return entity;
    }

}

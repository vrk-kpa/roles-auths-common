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

import fi.vm.kapa.rova.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.kapa.rova.logging.Logger.REQUEST_ID;

@Provider
public class SystemExceptionMapper extends AbstractExceptionMapper<SystemException> {
    private static final Logger LOG = Logger.getLogger(SystemExceptionMapper.class);

    @Override
    public Response toResponse(SystemException e) {
        LOG.error("SystemException: " + e.toString());
        return getResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
    }

    @Override
    protected Map<String, Object> buildEntity(SystemException e) {
        Map<String, Object> entity = new HashMap<>(2);
        entity.put(REQUEST_ID, fetchRequestId());
        entity.put("errorMessage", e.getMessage());
        entity.put("errorCode", e.getCodeNumber());
        return entity;
    }

}

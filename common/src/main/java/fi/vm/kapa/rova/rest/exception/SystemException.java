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

import java.util.HashMap;
import java.util.Map;

public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 5666076931422150523L;

    public static final String MSG_FAIL = "operaatio.epaonnistui";

    private final ExceptionType exceptionType;

    private final Map<Key, String> data = new HashMap<>();

    public enum Key {
        FIELD, VALUE, MIN, MAX, DESCRIPTION
    }

    public SystemException(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public SystemException(Exception cause, ExceptionType exceptionType) {
        super(cause);
        this.exceptionType = exceptionType;
    }

    public SystemException set(Key key, String value) {
        data.put(key, value);
        return this;
    }

    public String get(Key key) {
        return data.get(key);
    }

    public int getCodeNumber() {
        return exceptionType.getCodeNumber();
    }

    public String getCodeName() {
        return exceptionType.toString();
    }

    public String getMessage() {
        return exceptionType.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ExceptionType: ");
        if (null != exceptionType) {
            builder.append(getCodeNumber())
                    .append(" ")
                    .append(getCodeName());
        } else {
            builder.append("null");
        }
        builder.append("\n").append("data: {\n");
        int count = 0;
        for (Map.Entry<Key, String> entry : data.entrySet()) {
            String key = entry.getKey().toString();
            builder.append(key).append(" : ").append(entry.getValue());
            if (++count < data.size()) {
                builder.append(",");
            }
            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }
}

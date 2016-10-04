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

package fi.vm.kapa.rova.ui;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import fi.vm.kapa.rova.rest.exception.ExceptionType;
import fi.vm.kapa.rova.rest.exception.SystemException;
import fi.vm.kapa.rova.rest.exception.SystemException.Key;

import java.util.HashMap;
import java.util.Map;

public enum Channel {

    ADMIN_UI("ADMIN-UI"),
    WEB_API("WEB-API"),
    VARE_UI("VARE-UI");
    
    private static class Holder {
        static Map<String, Channel> MAP = new HashMap<>();
    }

    private String name;

    Channel(String name) {
        this.name = name;
        Holder.MAP.put(name, this);
    }

    @JsonValue
    public String getName() {
        return name;       
    }
    
    @JsonCreator
    public static Channel find(String val) {
        Channel t = Holder.MAP.get(val);
        if (t == null) {
            throw new SystemException(ExceptionType.MISSING_PARAMETER)
            .set(Key.DESCRIPTION, String.format("Unsupported type %s.", val));
        }
        return t;
    }
    
    @Override
    public String toString() {
        return name;
    }
}


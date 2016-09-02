
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


package fi.vm.kapa.rova.rest.exception;

import java.util.HashMap;
import java.util.Map;

public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 5666076931422150523L;

    private ExceptionType exceptionType;

    private Map<Key, String> data = new HashMap<Key, String>();

    public enum Key {
        FIELD, VALUE, MIN, MAX, DESCRIPTION
    }

    public SystemException(ExceptionType exceptionType) {
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

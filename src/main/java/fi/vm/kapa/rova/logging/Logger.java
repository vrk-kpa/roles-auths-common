package fi.vm.kapa.rova.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Logger {

    public enum Level {
        DEBUG, INFO, WARNING, ERROR
    }

    public enum Field {
        SERVICE("service"), // Name of logging application
        TYPE("type"), // log type
        ACTION("action"), // admin-ui action
        AUTHORIZATION_RESULT("authorization_result"), // allowed/disallowed-tulos
        CHANGES("changes"), // admin-ui list of former changed values
        DELEGATE_ID("delegate_id"), // delegaatin syntymäpäivä
        DELEGATE_NAME("delegate_name"), // delegaatin nimi
        DURATION("duration"), // kokonaiskesto
        END_USER("end_user"), // palvelun loppukäyttäjä
        ERRORSTR("error"), // virheviesti
        MSG("msg"), // yleinen viesti
        PRINCIPAL_COUNT("principal_count"), //  principalcount (jos delegate-haku)
        PRINCIPAL_ID("principal_id"), // päämiehen syntymäaika
        PRINCIPAL_NAME("principal_name"), // päämiehen nimi
        PRINCIPAL_LIST("principal_list"), // lista delegaatin päämiehistä
        REASONS("reasons"), // lista kieltoperusteista jos määritelty serviceen
        SERVICE_ID("xrd_service_id"), // xroad service id of calling service
        SERVICE_IDENTIFIER("xrd_serviceIdentifier"), // admin-ui serviceIdentifier of handled service
        SERVICE_REQUEST_IDENTIFIER("xrd_service_request_identifier"),
        WARNINGSTR("warning") // varoitusviesti
//        SERVICE("service")
//        changes
        ;

        private String value;
        Field(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return value;
        }
    }

    private org.slf4j.Logger slf4jLogger; // actual logger inside this wrapper

    public static final String REQUEST_ID = "ReqID";

    private Logger() {
    }

    public static Logger getLogger(Class cls) {
        Logger logger = new Logger();
        logger.slf4jLogger = LoggerFactory.getLogger(cls);
        return logger;
    }

    public void debug(String msg, Object... args) {
        if (slf4jLogger.isDebugEnabled()) {
            slf4jLogger.debug(createMessage(msg, args));
        }
    }

    public LogMap debugMap() {
        return new LogMap(Level.DEBUG, this);
    }

    private void info(String msg) {
        slf4jLogger.info(createMessage(msg));
    }

    public void info(String msg, Object... args) {
        if (slf4jLogger.isInfoEnabled()) {
            slf4jLogger.info(createMessage(msg, args));
        }
    }

    public LogMap infoMap() {
        return new LogMap(Level.INFO, this);
    }

    public void warning(String msg) {
        slf4jLogger.warn(createMessage(msg));
    }

    public void warning(String msg, Object... args)  {
        if (slf4jLogger.isWarnEnabled()) {
            slf4jLogger.warn(createMessage(msg, args));
        }
    }

    public LogMap warningMap() {
        return new LogMap(Level.WARNING, this);
    }

    public void error(String msg) {
        slf4jLogger.error(createMessage(msg));
    }

    public void error(String msg, Object... args) {
        if (slf4jLogger.isErrorEnabled()) {
            slf4jLogger.error(createMessage(msg, args));
        }
    }

    public void error(String msg, Exception e) {
        slf4jLogger.error(msg, e);
    }

    public LogMap errorMap() {
        return new LogMap(Level.ERROR, this);
    }

    public boolean isDebugEnabled() {
        return slf4jLogger.isDebugEnabled();
    }

    public static String maskHetuEnding(String hetu) {
        if (hetu == null) {
            return hetu;
        }
        return hetu.replaceAll("(\\d{6})(-|A|\\+)\\d{3}.(?![A-Z0-9])", "$1$2XXXX");
    }
    
    private String createMessage(String msg, Object... args) {
        String realMsg;
        if (args != null && args.length > 0) {
            realMsg = String.format(msg, args);
        } else {
            realMsg = msg;
        }
        return maskHetuEnding(realMsg);
    }

    public static class LogMap {
        private HashMap<String, Object> entries;
        private Level level;
        private Logger logger;
        private ObjectMapper mapper;

        public LogMap(Level level, Logger logger) {
            this.level = level;
            this.logger = logger;
            this.entries = new HashMap<>();
            this.mapper = new ObjectMapper();
        }

        public LogMap set(String key, Object value) {
            Validate.notBlank(key);
            Validate.isTrue( ! entries.containsKey(key), "LogMap already contains key %s", key);
            entries.put(key, value);
            return this;
        }

        public LogMap set(Field field, Object value) {
            return this.set(field.toString(), value);
        }

        public LogMap add(String key, Object value) {
            if (!entries.containsKey(key)) {
                return set(key, value);
            }
            Object oldValue = entries.get(key);
            if (Collection.class.isAssignableFrom(oldValue.getClass())) {
                Collection coll = (Collection) oldValue;
                coll.add(value);
            } else {
                ArrayList<Object> newValue = new ArrayList<>();
                newValue.add(value);
                newValue.add(oldValue);
                entries.put(key, newValue);
            }
            return this;
        }

        public LogMap add(Field field, Object value) {
            return add(field.toString(), value);
        }

        public Object unset(String key) {
            return entries.remove(key);
        }

        public Object unset(Field field) {
            return unset(field.toString());
        }

        public LogMap level(Level level) {
            this.level = level;
            return this;
        }

        public void log() {
            if (!logger.isEnabled(this.level)) {
                return;
            }
            String logJson;
            try {
               logJson = mapper.writeValueAsString(entries);
            } catch (JsonProcessingException e) {
                logger.error("Logging map failed", e);
                throw new RuntimeException(e);
            }
            switch (level) {
                case DEBUG:
                    logger.debug(logJson);
                    break;
                case INFO:
                    logger.info(logJson);
                    break;
                case WARNING:
                    logger.warning(logJson);
                    break;
                case ERROR:
                    logger.error(logJson);
                    break;
                default:
                    throw new IllegalArgumentException("Level " + level + " is not recognized");
            }
        }
    }

    private boolean isEnabled(Level level) {
        boolean isEnabled = false;
        switch (level) {
            case DEBUG:
                isEnabled = slf4jLogger.isDebugEnabled();
                break;
            case INFO:
                return slf4jLogger.isInfoEnabled();
            case WARNING:
                return slf4jLogger.isWarnEnabled();
            case ERROR:
                return slf4jLogger.isErrorEnabled();
        }
        return isEnabled;
//        throw new IllegalArgumentException("Unknown loglevel: "+level);
    }

    // For testing
    public void setSlf4jLogger(org.slf4j.Logger logger) {
        this.slf4jLogger = logger;
    }
}

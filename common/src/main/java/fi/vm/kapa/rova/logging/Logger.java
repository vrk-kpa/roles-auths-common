package fi.vm.kapa.rova.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Logger {

    /**
     * {@value} 
     */
    public static final int MAX_STACKTRACE_LENGTH = 30; 

    public enum Level {
        DEBUG, INFO, WARNING, ERROR
    }

    public enum Field {
        ACTION("action"), // admin-ui action
        CHANGES("changes"), // admin-ui list of former changed values
        DELEGATE_ID("delegate_id"), // delegaatin syntymäpäivä
        DELEGATE_NAME("delegate_name"), // delegaatin nimi
        DURATION("duration"), // kokonaiskesto
        END_USER("end_user"), // palvelun loppukäyttäjä
        ERRORSTR("error"), // virheviesti
        ISSUES("issues"), // valtakirjan (mandaten) asia(t)
        MANDATE_UUID("mandate_uuid"), // valtakirjan (mandaten) id
        MSG("msg"), // yleinen viesti
        OPERATION("operation"),
        PRINCIPAL_COUNT("principal_count"), //  principalcount (jos delegate-haku)
        PRINCIPAL_ID("principal_id"), // päämiehen syntymäaika
        PRINCIPAL_NAME("principal_name"), // päämiehen nimi
        PRINCIPAL_LIST("principal_list"), // lista delegaatin päämiehistä
        ORGANIZATION_NAME("organization_name"),
        ORGANIZATION_ID("organization_id"),
        ORGANIZATION_ROLES("organization_roles"),
        REASONS("reasons"), // lista kieltoperusteista jos määritelty serviceen
        RESULT("result"), // allowed/disallowed-tulos
        SERVICE("service"), // Name of logging application
        SERVICE_UUID("service_uuid"), // service universally unique identifier
        SERVICE_ID("xrd_service_id"), // xroad service id of calling service
        SERVICE_IDENTIFIER("xrd_serviceIdentifier"), // admin-ui serviceIdentifier of handled service
        SERVICE_WEB_API_ID("web_api_service_id"), // service web api identifier
        SERVICE_REQUEST_IDENTIFIER("xrd_service_request_identifier"),
        SUBJECT("subject"), // valtakirjan (mandaten) tarkemmin yksilöivä tunniste
        TARGET_USER("target_user"),
        TYPE("type"), // log type
        WARNINGSTR("warning"), // varoitusviesti
        STACKTRACE("stacktrace")
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

    public LogMap debugMap() {
        return new LogMap(Level.DEBUG, this);
    }

    public LogMap infoMap() {
        return new LogMap(Level.INFO, this);
    }

    public LogMap errorMap() {
        return new LogMap(Level.ERROR, this);
    }

    public LogMap warningMap() {
        return new LogMap(Level.WARNING, this);
    }

    public void debug(String msg) {
        debugMap().set(Field.MSG, msg).log();
    }

    public void info(String msg) {
        infoMap().set(Field.MSG, msg).log();
    }

    public void warning(String msg) {
        warningMap().set(Field.WARNINGSTR, msg).log();
    }

    public void error(String msg) {
        errorMap().set(Field.ERRORSTR, msg).log();
    }

    public void debug(String msg, Object... args) {
        if (slf4jLogger.isDebugEnabled()) {
            String message = createMessage(msg, args);
            debugMap().set(Field.MSG, message).log();
        }
    }

    public void info(String msg, Object... args) {
        if (slf4jLogger.isInfoEnabled()) {
            String message = createMessage(msg, args);
            infoMap().set(Field.MSG, message).log();
        }
    }

    public void warning(String msg, Object... args)  {
        if (slf4jLogger.isWarnEnabled()) {
            String message = createMessage(msg, args);
            warningMap().set(Field.WARNINGSTR, message).log();
        }
    }

    public void error(String msg, Object... args) {
        if (slf4jLogger.isErrorEnabled()) {
            String message = createMessage(msg, args);
            errorMap().set(Field.ERRORSTR, message).log();
        }
    }

    public void error(String msg, Exception e) {
        String message = createMessage(msg);
        String stackTrace = createStackTrace(e);
        errorMap()
            .set(Field.ERRORSTR, message)
            .set(Field.STACKTRACE, stackTrace)
            .log();
    }

    public boolean isDebugEnabled() {
        return slf4jLogger.isDebugEnabled();
    }

    private void logDebug(String msg) {
        slf4jLogger.debug(msg);
    }

    private void logInfo(String msg) {
        slf4jLogger.info(msg);
    }

    private void logWarning(String msg) {
        slf4jLogger.warn(msg);
    }

    private void logError(String msg) {
        slf4jLogger.error(msg);
    }

    public static String createStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        String message = sw.toString();
        String [] arr = message.split("\\n+");
        String [] limited = Arrays.copyOf(arr,
             ((MAX_STACKTRACE_LENGTH < arr.length) ? MAX_STACKTRACE_LENGTH : arr.length)
        );
        return String.join("\n", limited);
    }

    private String createMessage(String msg, Object... args) {
        String realMsg;
        if (args != null && args.length > 0) {
            realMsg = String.format(msg, args);
        } else {
            realMsg = msg;
        }
        return realMsg;
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
                    logger.logDebug(logJson);
                    break;
                case INFO:
                    logger.logInfo(logJson);
                    break;
                case WARNING:
                    logger.logWarning(logJson);
                    break;
                case ERROR:
                    logger.logError(logJson);
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

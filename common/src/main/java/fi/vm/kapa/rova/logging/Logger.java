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
package fi.vm.kapa.rova.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public final class Logger {

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
        END_DATE("end_date"), // (valtuuden) alkupäivämäärä
        END_USER("end_user"), // palvelun loppukäyttäjä
        ERRORSTR("error"), // virheviesti
        ISSUES("issues"), // valtuuden (mandaten) asia(t)
        MANDATE_UUID("mandate_uuid"), // valtuuden (mandaten) id
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
        SERVICE_WEB_API_SESSION_ID("web_api_session_id"), // service web api identifier
        SERVICE_REQUEST_IDENTIFIER("xrd_service_request_identifier"),
        START_DATE("start_date"), // (valtuuden) loppupäivämäärä
        SUBJECT("subject"), // valtuuden (mandaten) tarkemmin yksilöivä tunniste
        TARGET_USER("target_user"),
        TYPE("type"), // log type
        WARNINGSTR("warning"), // varoitusviesti
        STACKTRACE("stacktrace"),
        CLIENT_IP("client_ip"),
        SOCIAL_SECURITY_NUMBER("social_security_number"),//hetu
        PERSON_FIRSTNAMES("person_firstnames"),
        AUTHENTICATION_ASSERTION("authentication_assertion"),
        PERSON_LASTNAME("person_lastname");

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

    @SuppressWarnings("squid:S1148") // don't complain about printStackTrace()
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
        private Map<String, Object> entries;
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
                List<Object> newValue = new ArrayList<>();
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
            if (!isEnabled(this.level)) {
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
                    logger.slf4jLogger.debug(logJson);
                    break;
                case INFO:
                    logger.slf4jLogger.info(logJson);
                    break;
                case WARNING:
                    logger.slf4jLogger.warn(logJson);
                    break;
                case ERROR:
                    logger.slf4jLogger.error(logJson);
                    break;
                default:
                    throw new IllegalArgumentException("Level " + level + " is not recognized");
            }
        }

        private boolean isEnabled(Level level) {
            switch (level) {
                case DEBUG:
                    return logger.slf4jLogger.isDebugEnabled();
                case INFO:
                    return logger.slf4jLogger.isInfoEnabled();
                case WARNING:
                    return logger.slf4jLogger.isWarnEnabled();
                case ERROR:
                    return logger.slf4jLogger.isErrorEnabled();
                default:
                    throw new IllegalArgumentException("Unknown loglevel: "+level);
            }
        }
    }

    // For testing
    public void setSlf4jLogger(org.slf4j.Logger logger) {
        this.slf4jLogger = logger;
    }
}

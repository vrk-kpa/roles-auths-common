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

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.access.tomcat.LogbackValve;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import net.logstash.logback.appender.LogstashAccessTcpSocketAppender;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Juha Korkalainen on 16.9.2015.
 */
@Component
public class LogbackConfigurator {

    @Value("${logstash.host:localhost}")
    private String logstashHost;

    @Value("${logstash.port:5000}")
    private int logstashPort;

    @Value("${logstash.accesslog.port:0}")
    private int logstashAccessLogPort;

    @Value("${service.name}")
    private String serviceName;

    @Value("${logstash.level:INFO}")
    private String logLevel;

    @Value(value = "${application.access-log:true}")
    protected Boolean accessLog;

    @Value(value = "${console.logging:false}")
    protected Boolean consoleLog;

    @PostConstruct
    public void initLogging() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        LogstashEncoder enc = new LogstashEncoder();
        enc.setCustomFields("{\""+Logger.Field.SERVICE+"\":\"" + serviceName + "\", \""+Logger.Field.TYPE+"\": \"application_log\"}");
        enc.setContext(lc);

        LogstashTcpSocketAppender logStashAppender = new LogstashTcpSocketAppender();
        logStashAppender.setRemoteHost(logstashHost);
        logStashAppender.setPort(logstashPort);
        logStashAppender.setEncoder(enc);
        logStashAppender.setContext(lc);
        logStashAppender.setName("logstash_application");
        logStashAppender.start();

        // ROOT LOGGER To use logstash
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        logger.addAppender(logStashAppender);
        if (consoleLog) {
            logger.addAppender(new ConsoleAppender<>());
        }
        logger.setLevel(Level.toLevel(logLevel));
        logger.setAdditive(true); /* set to true if root should log too */
   }

    public EmbeddedServletContainerCustomizer containerCustomizer(){
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                if(container instanceof TomcatEmbeddedServletContainerFactory){
                    TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
                    LogbackValve logbackAccessValve = new LogbackValve();

                    logbackAccessValve.addAppender(getAccessLogAppender());
                    containerFactory.addContextValves(logbackAccessValve);
                }
            }

            private Appender<IAccessEvent> getAccessLogAppender() {
                LoggerContext lc = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
                LayoutWrappingEncoder enc = new LayoutWrappingEncoder();
                enc.setContext(lc);
                RovaLogstashAccessLayout layout = new RovaLogstashAccessLayout();
                layout.setContext(lc);
                enc.setLayout(layout);
                layout.start();

                LogstashAccessTcpSocketAppender logStashAxsAppender = new LogstashAccessTcpSocketAppender();
                logStashAxsAppender.setRemoteHost(logstashHost);
                // default to logstashPort if a separate port for access log is not defined 
                logStashAxsAppender.setPort(logstashAccessLogPort != 0 ? logstashAccessLogPort : logstashPort);
                logStashAxsAppender.setEncoder(enc);
                logStashAxsAppender.setContext(lc);
                logStashAxsAppender.setName("logstash_access");

                logStashAxsAppender.start();
                return logStashAxsAppender;
            }
        };
    }


}
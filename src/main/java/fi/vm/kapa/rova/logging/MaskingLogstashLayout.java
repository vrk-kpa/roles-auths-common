package fi.vm.kapa.rova.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.layout.LogstashLayout;

/**
 * Created by Juha Korkalainen on 17.9.2015.
 */
public class MaskingLogstashLayout extends LogstashLayout {

    @Override
    public String doLayout(ILoggingEvent iLoggingEvent) {
        return Logger.maskHetuEnding(super.doLayout(iLoggingEvent)) + "\n";
    }

}

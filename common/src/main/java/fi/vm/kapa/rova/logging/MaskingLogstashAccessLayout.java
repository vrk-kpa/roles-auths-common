package fi.vm.kapa.rova.logging;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.layout.LogstashAccessLayout;

/**
 * Created by Juha Korkalainen on 17.9.2015.
 */
public class MaskingLogstashAccessLayout extends LogstashAccessLayout {
    @Override
    public String doLayout(IAccessEvent iAccessEvent) {
        return Logger.maskHetuEnding(super.doLayout(iAccessEvent)) + "\n";
    }

}

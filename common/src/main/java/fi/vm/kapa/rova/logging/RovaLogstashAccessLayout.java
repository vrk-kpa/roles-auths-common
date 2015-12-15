package fi.vm.kapa.rova.logging;

import ch.qos.logback.access.spi.IAccessEvent;
import net.logstash.logback.layout.LogstashAccessLayout;

/**
 * Created by Juha Korkalainen on 15.12.2015.
 */

public class RovaLogstashAccessLayout extends LogstashAccessLayout {
        @Override
        public String doLayout(IAccessEvent iAccessEvent) {
            return super.doLayout(iAccessEvent) + "\n";
        }
}

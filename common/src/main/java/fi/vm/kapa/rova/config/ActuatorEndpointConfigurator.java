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
package fi.vm.kapa.rova.config;


import fi.vm.kapa.rova.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ActuatorEndpointConfigurator implements InitializingBean {
    /**
     * {@value}
     */
    public static final String PROPERTY_SOURCE_NAME = "rovaActuatorSettings";

    private static final Logger LOG = Logger.getLogger(ActuatorEndpointConfigurator.class);

    @Autowired
    Environment env;

    @Override
    public void afterPropertiesSet() {
        MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        for (PropertySource source : sources) {
            if (source.getName().toLowerCase().contains("actuatorConfig.properties".toLowerCase())) {
                LOG.info("Removing default actuator settings: " + source.getName());
                sources.remove(source.getName());
            }
        }
        LOG.info("Adding new property source for actuator settings: " + PROPERTY_SOURCE_NAME);
        Map<String, Object> settings = new HashMap<>();
        // Common actuator specific endpoint settings.
        settings.put("endpoints.enabled", false);
        settings.put("endpoints.info.enabled", true);
        sources.addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, settings));
    }

}
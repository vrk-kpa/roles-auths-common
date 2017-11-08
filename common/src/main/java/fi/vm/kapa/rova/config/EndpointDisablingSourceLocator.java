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
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Disables Spring Cloud configuration specific endpoints by adding a new property source locator.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EndpointDisablingSourceLocator implements PropertySourceLocator {
    /**
     * {@value}
     */
    public static final String PROPERTY_SOURCE_NAME = "rovaEndpointDisablingSettings";

    private static final Logger LOG = Logger.getLogger(EndpointDisablingSourceLocator.class);

    @Override
    public PropertySource<?> locate(Environment environment) {
        LOG.info("Adding property source for disabling endpoints: " + PROPERTY_SOURCE_NAME);
        Map<String, Object> settings = new HashMap<>();
        // This also disables cloud config related endpoints from 1.2.2 (from Dalston.SR4) on.
        settings.put("endpoints.enabled", false);
        // Spring Cloud Config specific endpoint settings.
        settings.put("endpoints.pause.enabled", false);
        settings.put("endpoints.resume.enabled", false);
        settings.put("endpoints.refresh.enabled", false);
        settings.put("endpoints.restart.enabled", false);
        settings.put("endpoints.env.post.enabled", false);
        return new MapPropertySource(PROPERTY_SOURCE_NAME, settings);
    }

}
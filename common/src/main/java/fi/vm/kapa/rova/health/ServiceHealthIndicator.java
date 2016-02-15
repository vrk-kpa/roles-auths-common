package fi.vm.kapa.rova.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class ServiceHealthIndicator extends AbstractHealthIndicator {

    @Value("${service.name}")
    private String description;

    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up();
        builder.withDetail("name", description);
    }
}
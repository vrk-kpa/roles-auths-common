package fi.vm.kapa.rova.engine.evaluation;

import fi.vm.kapa.rova.health.GitEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:/actuatorConfig.properties")
})
public class BaseSpringConfig {

    @Bean
    public GitEndpoint getGitEndpoint() {
        return new GitEndpoint();
    }
}

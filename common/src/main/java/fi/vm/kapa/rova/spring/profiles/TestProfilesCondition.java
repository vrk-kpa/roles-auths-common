package fi.vm.kapa.rova.spring.profiles;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * Created by jkorkala on 07/03/2017.
 */
public class TestProfilesCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        return environment.acceptsProfiles(SpringProfiles.DEV, SpringProfiles.TEST);
    }

}

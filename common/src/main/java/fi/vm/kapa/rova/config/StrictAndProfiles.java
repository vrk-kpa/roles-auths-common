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

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

public class StrictAndProfiles implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        if (env == null) {
            return true;
        }

        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles == null || activeProfiles.length == 0) {
            return true;
        }

        MultiValueMap<String, Object> profilesAnnotation = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (profilesAnnotation == null || profilesAnnotation.isEmpty()) {
            throw new IllegalStateException("Strict profile condition requires profiles annotation");
        }

        boolean match = true;
        for (Object profiles : profilesAnnotation.get("value")) {
            for (String profile : (String[]) profiles) {
                for (String activeProfile : activeProfiles) {
                    match = match && profileMatch(activeProfile, profile);
                }
            }
        }
        return match;
    }

    private boolean profileMatch(String activeProfile, String profile) {
        boolean profileNegated = profile.startsWith("!");
        String realProfile = profile.replaceFirst("!", "");
        boolean activeNegated = activeProfile.startsWith("!");
        String realActiveProfile = activeProfile.replaceFirst("!", "");

        if ((!profileNegated && !activeNegated) || (profileNegated && activeNegated)) {
            return realProfile.equals(realActiveProfile);
        }
        return !realProfile.equals(realActiveProfile);
    }
}

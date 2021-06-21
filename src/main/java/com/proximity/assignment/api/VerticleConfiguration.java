package com.proximity.assignment.api;

import com.proximity.assignment.verticles.ApiVerticle;
import com.proximity.assignment.verticles.SetupVerticle;
import com.proximity.assignment.verticles.SpringVerticleFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sthammishetty on 20/06/21
 */
@Configuration
public class VerticleConfiguration {

    @Bean
    public SetupVerticle getSetupVerticle() {
        return new SetupVerticle();
    }

    @Bean
    public SpringVerticleFactory getSpringVerticleFactory() {
        return new SpringVerticleFactory();
    }

    @Bean
    public ApiVerticle getApiVerticle() {
        return new ApiVerticle();
    }


}

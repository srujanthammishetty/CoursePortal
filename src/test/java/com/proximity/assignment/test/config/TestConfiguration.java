package com.proximity.assignment.test.config;

import com.proximity.assignment.api.ApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author sthammishetty on 21/06/21
 */
@Configuration
@Import(ApplicationConfiguration.class)
@ComponentScan(basePackages = "com.proximity.assignment.*")
public class TestConfiguration {
}

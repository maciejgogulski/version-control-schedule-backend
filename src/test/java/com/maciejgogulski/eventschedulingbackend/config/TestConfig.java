package com.maciejgogulski.eventschedulingbackend.config;

import org.flywaydb.test.FlywayTestExecutionListener;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListeners;

@TestConfiguration()
@TestExecutionListeners({
        FlywayTestExecutionListener.class
})
public class TestConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}

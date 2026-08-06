package com.foodwings.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * DataSource configuration.
 * The active Spring profile determines which database is used:
 *   (default) H2 in-memory database — works on Render free tier with no external DB.
 *   mysql     Railway / external MySQL database.
 *   dev       H2 with console enabled for local development.
 *
 * Set SPRING_PROFILES_ACTIVE=mysql on Render to use a real MySQL database.
 */
@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        log.info("Configuring DataSource: driver={}, url={}",
                properties.getDriverClassName(),
                properties.getUrl());
        DataSource ds = properties.initializeDataSourceBuilder().build();
        log.info("DataSource configured successfully.");
        return ds;
    }
}

package com.foodwings.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        String targetUrl = dbUrl;
        if (targetUrl != null && targetUrl.startsWith("mysql://")) {
            targetUrl = "jdbc:" + targetUrl;
        }

        log.info("Attempting database connection to: {}", targetUrl);

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(targetUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
            config.setDriverClassName(driverClassName);
            config.setConnectionTimeout(5000);
            config.setValidationTimeout(3000);
            config.setInitializationFailTimeout(-1);

            HikariDataSource ds = new HikariDataSource(config);
            try (Connection conn = ds.getConnection()) {
                log.info("Successfully connected to primary MySQL database!");
                return ds;
            } catch (Throwable t) {
                log.warn("Primary MySQL connection failed ({}), initializing H2 database fallback...", t.getMessage());
                try {
                    ds.close();
                } catch (Throwable ignored) {}
            }
        } catch (Throwable t) {
            log.warn("Error creating primary HikariDataSource ({}), using fallback...", t.getMessage());
        }

        // Resilient fallback to embedded H2 database in MySQL mode
        log.info("Starting fallback H2 database in MySQL mode...");
        HikariConfig fallbackConfig = new HikariConfig();
        fallbackConfig.setJdbcUrl("jdbc:h2:mem:foodwings;DB_CLOSE_DELAY=-1;MODE=MySQL");
        fallbackConfig.setUsername("sa");
        fallbackConfig.setPassword("");
        fallbackConfig.setDriverClassName("org.h2.Driver");
        return new HikariDataSource(fallbackConfig);
    }
}

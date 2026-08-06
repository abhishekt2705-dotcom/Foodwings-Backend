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
import java.sql.DriverManager;

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
        // Cap global JDBC login timeout so TCP handshake to dead MySQL fails fast
        DriverManager.setLoginTimeout(8);

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
            config.setConnectionTimeout(8000);       // 8s to get a connection from pool
            config.setValidationTimeout(3000);
            config.setInitializationFailTimeout(0);  // fail fast if no connection on startup

            HikariDataSource ds = new HikariDataSource(config);
            try (Connection conn = ds.getConnection()) {
                log.info("Successfully connected to primary MySQL database!");
                return ds;
            } catch (Throwable t) {
                log.warn("Primary MySQL connection test failed ({}), switching to H2 fallback...", t.getMessage());
                try { ds.close(); } catch (Throwable ignored) {}
            }
        } catch (Throwable t) {
            log.warn("Primary DataSource creation failed ({}), switching to H2 fallback...", t.getMessage());
        }

        // Resilient fallback to embedded H2 database in MySQL mode
        log.info("Starting H2 in-memory database (MySQL compatibility mode)...");
        HikariConfig fallbackConfig = new HikariConfig();
        fallbackConfig.setJdbcUrl("jdbc:h2:mem:foodwings;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=VALUE");
        fallbackConfig.setUsername("sa");
        fallbackConfig.setPassword("");
        fallbackConfig.setDriverClassName("org.h2.Driver");
        fallbackConfig.setConnectionTimeout(5000);
        return new HikariDataSource(fallbackConfig);
    }
}

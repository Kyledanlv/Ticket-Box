package com.example.ticketboxanalytics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
public class ReadOnlyDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.analytics")
    public DataSource analyticsDataSource() {
        // Địa chỉ này nên trỏ đến một Read-Only Replica hoặc một DB khác
        return DataSourceBuilder.create()
                .url("jdbc:mysql://analytics-replica:3306/TICKET_BOX?readOnly=true")
                .username("analytics_user")
                .password("analytics_password")
                .build();
    }

    @Bean
    public JdbcTemplate analyticsJdbcTemplate(DataSource analyticsDataSource) {
        return new JdbcTemplate(analyticsDataSource);
    }
}
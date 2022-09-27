package org.endeavourhealth.setup;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.yml")

public class HirakiCPDataSource {

    private Environment environment;

    @Bean
    @Qualifier("dataSource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(environment.getRequiredProperty("url"));
        dataSource.setUsername(environment.getRequiredProperty("username"));
        dataSource.setPassword(environment.getRequiredProperty("password"));

//        dataSource.setIdleTimeout(Long.valueOf(environment.getProperty("maxIdle")));
//        dataSource.setMaximumPoolSize(Integer.valueOf(environment.getProperty("maxPoolSize") == null ? ));

        return dataSource;
    }
}

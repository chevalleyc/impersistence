package org.endeavourhealth.setup;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.yml")
public class QuadStore {


    public class ExceptionTranslator implements ExecuteListener {
        public void exception(ExecuteContext context) {
            SQLDialect dialect = context.configuration().dialect();
            SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
            context.exception(translator.translate("Access database using Jooq", context.sql(), context.sqlException()));
        }
    }


    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        //TODO: make it multi-datasource based on configuration!
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(environment.getRequiredProperty("url"));
        dataSource.setUsername(environment.getRequiredProperty("username"));
        dataSource.setPassword(environment.getRequiredProperty("password"));

//        dataSource.setIdleTimeout(Long.valueOf(environment.getProperty("maxIdle")));
//        dataSource.setMaximumPoolSize(Integer.valueOf(environment.getProperty("maxPoolSize") == null ? ));

        return dataSource;
    }

    @Bean
    public TransactionAwareDataSourceProxy transactionAwareDataSource() {
        return new TransactionAwareDataSourceProxy(dataSource());
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(transactionAwareDataSource());
    }

    @Bean
    public ExceptionTranslator exceptionTransformer() {
        return new ExceptionTranslator();
    }

    @Bean
    @Primary
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    @Bean
    public DefaultConfiguration configuration() {

        SQLDialect dialect;
        String defaultDialect = "POSTGRES";

        try {
            dialect = SQLDialect.valueOf(defaultDialect);
        } catch (Exception e){
            throw  new IllegalArgumentException("only POSTGRES or YUGABYTEDB are supported");
        }

        //at the moment: SQLDialect.POSTGRES or SQLDialect.YUGABYTEDB
        if (dialect != SQLDialect.POSTGRES && dialect != SQLDialect.YUGABYTEDB)
            throw new IllegalArgumentException("only SQLDialect.POSTGRES or SQLDialect.YUGABYTEDB are supported");

        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider());
        jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));

        jooqConfiguration.set(dialect);

        return jooqConfiguration;
    }

    public DSLContext dslContext(){
        return configuration().dsl();

    }
}

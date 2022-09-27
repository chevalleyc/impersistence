package org.endeavourhealth.setup;

import com.opentable.db.postgres.embedded.FlywayPreparer;
import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.PreparedDbRule;
import com.opentable.db.postgres.junit.SingleInstancePostgresRule;
import org.jooq.impl.DefaultConfiguration;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.Statement;

import static org.endeavourhealth.Tables.NODE;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {QuadStore.class})
@TestPropertySource(properties = { "spring.config.location=classpath:application.yml" })

public class TestQuadStoreIT {

    private QuadStore quadstore;

    @ClassRule
    public static PreparedDbRule db = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation("db/migration"));

    @Rule
    public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

    @Autowired
    public void setQuadstore(QuadStore quadstore){
        this.quadstore = quadstore;
    }

    @Test
    @Sql(scripts = "createdb.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDatasource(){
        DefaultConfiguration defaultConfiguration = quadstore.configuration();
        Assertions.assertNotNull(defaultConfiguration);
        quadstore.dslContext().selectFrom(NODE).limit(1).fetch();
    }

}
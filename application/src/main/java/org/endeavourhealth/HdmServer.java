package org.endeavourhealth;

import org.endeavourhealth.configuration.ComponentConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {
        ManagementWebSecurityAutoConfiguration.class,
        R2dbcAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
@Import({
        ComponentConfiguration.class
})
public class HdmServer {

    public static void main(String[] args) {
        SpringApplication.run(HdmServer.class, args);
    }
}

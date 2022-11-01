package org.endeavourhealth.configuration;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"org.endeavourhealth.service"})
@EnableAspectJAutoProxy
public class
ComponentConfiguration {
}

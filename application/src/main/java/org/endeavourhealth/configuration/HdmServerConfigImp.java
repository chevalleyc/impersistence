package org.endeavourhealth.configuration;


import org.endeavourhealth.service.HdmServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Configuration
@ConfigurationProperties(prefix = "server")
public class HdmServerConfigImp implements HdmServerConfig {

    @Min(1025)
    @Max(65536)
    private int port;
    private String host;

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

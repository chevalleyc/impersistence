package org.endeavourhealth.service;

public interface HdmServerConfig {
    int getPort();
    void setPort(int port);
    String getHost();
    void setHost(String host);
}

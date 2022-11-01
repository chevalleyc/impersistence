package org.endeavourhealth.service;


import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class BaseServiceImp implements BaseService{

    private final HdmServerConfig serverConfig;
    private final DSLContext context;

    private UUID systemId;

    @Autowired
    public BaseServiceImp(DSLContext context,
                          HdmServerConfig serverConfig) {
        this.context = context;
        this.serverConfig = serverConfig;
    }

    @Override
    public HdmServerConfig getServerConfig() {
        return null;
    }
}

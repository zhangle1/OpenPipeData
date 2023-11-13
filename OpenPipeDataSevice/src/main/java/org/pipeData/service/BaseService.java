package org.pipeData.service;

import org.pipeData.common.MessageResolver;
import org.pipeData.security.manager.OpenPipeSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService  extends MessageResolver {


    protected OpenPipeSecurityManager securityManager;


    @Autowired
    public void setSecurityManager(OpenPipeSecurityManager openPipeSecurityManager) {
        this.securityManager = openPipeSecurityManager;
    }

}

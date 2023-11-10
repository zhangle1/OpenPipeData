package org.pipeData.controller;

import org.pipeData.common.MessageResolver;
import org.pipeData.security.manager.OpenPipeSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController extends MessageResolver {


    protected OpenPipeSecurityManager securityManager;

    @Autowired
    public void setSecurityManager(OpenPipeSecurityManager securityManager) {
        this.securityManager = securityManager;
    }

}

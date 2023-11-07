package org.pipeData.security.manager;

import org.pipeData.core.entity.User;
import org.pipeData.security.base.PasswordToken;
import org.pipeData.security.base.Permission;
import org.pipeData.security.exception.AuthException;
import org.pipeData.security.exception.PermissionDeniedException;

public interface OpenPipeSecurityManager {

    void login(PasswordToken token) throws AuthException;

    boolean validateUser(String username,String password) throws AuthException;

    String login(String jwtToken) throws AuthException;


    void logoutCurrent();

    boolean isAuthenticated();

    void requireAllPermissions(Permission... permission) throws PermissionDeniedException;

    void requireAnyPermission(Permission... permissions) throws PermissionDeniedException;

    void requireOrgOwner(String orgId) throws PermissionDeniedException;

    boolean isOrgOwner(String orgId);

    boolean hasPermission(Permission... permission);

    User getCurrentUser();

    void runAs(String userNameOrEmail);

    void releaseRunAs();

}

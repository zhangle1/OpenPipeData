package org.pipeData.security.manager;


import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.pipeData.security.base.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionDataCache {

    private final RequestScopePermissionDataCache requestScope;

    private final ThreadScopePermissionDataCache threadScope;

    public PermissionDataCache(RequestScopePermissionDataCache permissionDataCache,
                               ThreadScopePermissionDataCache threadScope) {
        this.requestScope = permissionDataCache;
        this.threadScope = threadScope;
    }


    public SimpleAuthorizationInfo getAuthorizationInfo() {
        try {
            return requestScope.getAuthorizationInfo();
        } catch (Exception e) {
            return threadScope.getAuthorizationInfo();
        }
    }

    public String getCurrentOrg() {
        try {
            return requestScope.getCurrentOrg();
        } catch (Exception e) {
            return threadScope.getCurrentOrg();
        }
    }

    public void setCurrentOrg(String currentOrg) {
        try {
            requestScope.setCurrentOrg(currentOrg);
        } catch (Exception e) {
            threadScope.setCurrentOrg(currentOrg);
        }
    }

    public Boolean getCachedPermission(Permission permission) {
        try {
            return requestScope.getCachedPermission(permission);
        } catch (Exception e) {
            return threadScope.getCachedPermission(permission);
        }
    }

    public void setPermissionCache(Permission permission, Boolean permitted) {
        try {
            requestScope.setPermissionCache(permission, permitted);
        } catch (Exception e) {
            threadScope.setPermissionCache(permission, permitted);
        }
    }


    public void setAuthorizationInfo(SimpleAuthorizationInfo authorizationInfo) {
        try {
            requestScope.setAuthorizationInfo(authorizationInfo);
        } catch (Exception e) {
            threadScope.setAuthorizationInfo(authorizationInfo);
        }
    }

    public SimpleAuthenticationInfo getAuthenticationInfo() {
        try {
            return requestScope.getAuthenticationInfo();
        } catch (Exception e) {
            return threadScope.getAuthenticationInfo();
        }
    }

    public void setAuthenticationInfo(SimpleAuthenticationInfo authenticationInfo) {
        try {
            requestScope.setAuthenticationInfo(authenticationInfo);
        } catch (Exception e) {
            threadScope.setAuthenticationInfo(authenticationInfo);
        }
    }

    public void clear() {
        threadScope.clear();
        try {
            requestScope.clear();
        } catch (Exception e) {
        }
    }

}

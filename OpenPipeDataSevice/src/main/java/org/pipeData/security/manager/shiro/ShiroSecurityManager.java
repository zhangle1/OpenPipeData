package org.pipeData.security.manager.shiro;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.pipeData.common.MessageResolver;
import org.pipeData.core.base.consts.Const;
import org.pipeData.core.base.exception.BaseException;
import org.pipeData.core.base.exception.Exceptions;
import org.pipeData.core.entity.User;
import org.pipeData.security.base.JwtToken;
import org.pipeData.security.base.PasswordToken;
import org.pipeData.security.base.Permission;
import org.pipeData.security.base.RoleType;
import org.pipeData.security.exception.AuthException;
import org.pipeData.security.exception.PermissionDeniedException;
import org.pipeData.security.manager.OpenPipeSecurityManager;
import org.pipeData.security.manager.PermissionDataCache;
import org.pipeData.security.util.JwtUtils;
import org.pipeData.service.IUserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component()
public class ShiroSecurityManager implements OpenPipeSecurityManager {

    @PostConstruct
    public void initSecurityManager() {
        SecurityUtils.setSecurityManager(securityManager);
    }
    final MessageResolver messageResolver;
    private final IUserService userService;
    private final PermissionDataCache permissionDataCache;


    private final SecurityManager securityManager;

    public ShiroSecurityManager(MessageResolver messageResolver,
                                IUserService userService, PermissionDataCache permissionDataCache,
                                SecurityManager securityManager) {
        this.messageResolver = messageResolver;
        this.userService = userService;
        this.permissionDataCache = permissionDataCache;
        this.securityManager = securityManager;
    }

    @Override
    public void login(PasswordToken token) throws RuntimeException {
        logoutCurrent();
       User user = userService.selectByNameOrEmail(token.getSubject());
        if (user == null) {
            Exceptions.tr(BaseException.class, "login.fail");
        }
        if (!user.getActive()) {
            Exceptions.tr(BaseException.class, "message.user.not.active");
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(token.getSubject(), token.getPassword());
        try {
            subject.login(usernamePasswordToken);
        } catch (Exception e) {
            log.error("Login error ({})", token.getSubject());
            Exceptions.msg("login.fail");
        }
    }

    @Override
    public boolean validateUser(String username, String password) throws AuthException {
//        User user = userMapper.selectByNameOrEmail(username);
        // TODO: 2023/11/8
        User user = new User();
        if (user == null) {
            return false;
        }
        return BCrypt.checkpw(password, user.getPassword()) || Objects.equals(password, user.getPassword());
    }

    @Override
    public String login(String tokenString) throws AuthException {
        logoutCurrent();
        JwtToken jwtToken = JwtUtils.toJwtToken(tokenString);
        if (!JwtUtils.validTimeout(jwtToken)) {
            Exceptions.tr(AuthException.class, "login.session.timeout");
        }
        User user = userService.selectByNameOrEmail(jwtToken.getSubject());
        if (user == null) {
            Exceptions.tr(AuthException.class, "login.session.timeout");
        }
        if (!user.getActive()) {
            Exceptions.tr(BaseException.class, "message.user.not.active");
        }

        if (jwtToken.getPwdHash() != user.getPassword().hashCode()) {
            Exceptions.tr(AuthException.class, "login.fail.pwd.hash");
        }

        BearerToken bearerToken = new BearerToken(tokenString);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(bearerToken);
        } catch (Exception e) {
            log.error("Login error ({})", user.getUsername());
            Exceptions.msg("login.fail");
        }
        return JwtUtils.toJwtString(JwtUtils.createJwtToken(user));
    }

    @Override
    public void logoutCurrent() {
        permissionDataCache.clear();
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
    }

    @Override
    public boolean isAuthenticated() {
        return SecurityUtils.getSubject().isAuthenticated();
    }

    @Override
    public void requireAllPermissions(Permission... permissions) throws PermissionDeniedException {
        for (Permission permission : permissions) {
            Boolean permitted = permissionDataCache.getCachedPermission(permission);
            if (permitted != null) {
                if (!permitted) {
                    Exceptions.e(new AuthorizationException());
                } else {
                    return;
                }
            }
            Set<String> permissionString = toShiroPermissionString(permission.getOrgId()
                    , permission.getRoleId()
                    , permission.getResourceType()
                    , permission.getResourceId()
                    , permission.getPermission());
            try {
                permissionDataCache.setCurrentOrg(permission.getOrgId());
                SecurityUtils.getSubject().checkPermissions(permissionString.toArray(new String[0]));
                permissionDataCache.setPermissionCache(permission, true);
            } catch (AuthorizationException e) {
                log.warn("User permission denied. User-{} Permission-{}"
                        , getCurrentUser() != null ? getCurrentUser().getUsername() : "none"
                        , permission);
                permissionDataCache.setPermissionCache(permission, false);
                Exceptions.tr(PermissionDeniedException.class, "message.security.permission-denied");
            }
        }
    }

    @Override
    public void requireAnyPermission(Permission... permissions) throws PermissionDeniedException {
        boolean anyMatch = Arrays.stream(permissions).anyMatch(permission -> {
            if (permission == null) {
                return false;
            }
            Boolean permitted = permissionDataCache.getCachedPermission(permission);
            if (permitted != null) {
                if (!permitted) {
                    Exceptions.e(new AuthorizationException());
                } else {
                    return true;
                }
            }
            Set<String> permissionString = toShiroPermissionString(permission.getOrgId()
                    , permission.getRoleId()
                    , permission.getResourceType()
                    , permission.getResourceId()
                    , permission.getPermission());
            try {
                permissionDataCache.setCurrentOrg(permission.getOrgId());
                SecurityUtils.getSubject().checkPermissions(permissionString.toArray(new String[0]));
                permissionDataCache.setPermissionCache(permission, true);
                return true;
            } catch (AuthorizationException e) {
                log.warn("User permission denied. User-{} Permission-{}"
                        , getCurrentUser() != null ? getCurrentUser().getUsername() : "none"
                        , permission);
                permissionDataCache.setPermissionCache(permission, false);
                return false;
            }
        });
        if (!anyMatch) {
            Exceptions.tr(PermissionDeniedException.class, "message.security.permission-denied");
        }
    }

    @Override
    public void requireOrgOwner(String orgId) throws PermissionDeniedException {
        try {
            permissionDataCache.setCurrentOrg(orgId);
            SecurityUtils.getSubject().checkRole(toShiroRoleString(RoleType.ORG_OWNER.name(), orgId));
        } catch (AuthorizationException e) {
            log.warn("User permission denied. User-{} Role-{}"
                    , getCurrentUser() != null ? getCurrentUser().getUsername() : "none"
                    , RoleType.ORG_OWNER.name());
            Exceptions.tr(PermissionDeniedException.class, "message.security.permission-denied");
        }
    }

    @Override
    public boolean isOrgOwner(String orgId) {
        permissionDataCache.setCurrentOrg(orgId);
        return SecurityUtils.getSubject().hasRole(toShiroRoleString(RoleType.ORG_OWNER.name(), orgId));
    }

    @Override
    public boolean hasPermission(Permission... permissions) {

        for (Permission permission : permissions) {

            Boolean permitted = permissionDataCache.getCachedPermission(permission);
            if (permitted != null) {
                return permitted;
            }

            Set<String> strings = toShiroPermissionString(permission.getOrgId()
                    , permission.getRoleId()
                    , permission.getResourceType()
                    , permission.getResourceId()
                    , permission.getPermission());
            try {
                permissionDataCache.setCurrentOrg(permission.getOrgId());
                SecurityUtils.getSubject().checkPermissions(strings.toArray(new String[0]));
                permissionDataCache.setPermissionCache(permission, true);
            } catch (AuthorizationException e) {
                log.debug("User permission denied. User-{} Permission-{}"
                        , getCurrentUser() != null ? getCurrentUser().getUsername() : "none"
                        , permission);
                permissionDataCache.setPermissionCache(permission, false);
                return false;
            }
        }
        return true;
    }

    @Override
    public User getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        return (User) subject.getPrincipal();
    }

    @Override
    public void runAs(String userNameOrEmail) {
        ThreadContext.unbindSubject();
        User user = userService.selectByNameOrEmail(userNameOrEmail);
        login(JwtUtils.toJwtString(JwtUtils.createJwtToken(user)));
    }

    @Override
    public void releaseRunAs() {
        logoutCurrent();
    }


    public static String toShiroRoleString(String roleType, String orgId) {
        return roleType + "." + orgId;
    }

    public static Set<String> toShiroPermissionString(String orgId, String roleId, String resourceType, String resourceId, int permission) {
        Set<String> shiroPermissionStrings = new HashSet<>();
        Set<String> permissions = expand2StringPermissions(permission);
        for (String p : permissions) {
            StringJoiner stringJoiner = new StringJoiner(":");
            stringJoiner.add(orgId)
                    .add(roleId != null ? roleId : "*")
                    .add(resourceType)
                    .add(p)
                    .add(resourceId);
            shiroPermissionStrings.add(stringJoiner.toString());
        }
        return shiroPermissionStrings;

    }

    public static String toShiroPermissionString(String orgId, String resourceType, String resourceId, String permission) {
        StringJoiner stringJoiner = new StringJoiner(":");
        stringJoiner.add(orgId)
                .add(resourceType)
                .add(permission)
                .add(resourceId);
        return stringJoiner.toString();
    }

    public static Set<String> expand2StringPermissions(int permission) {
        Set<String> permissions = new HashSet<>();
        if (permission == Const.DISABLE) {
            permissions.add("DISABLE");
            return permissions;
        }
        if ((Const.ENABLE & permission) == Const.ENABLE) {
            permissions.add("ENABLE");
        }
        if ((Const.READ & permission) == Const.READ) {
            permissions.add("READ");
        }
        if ((Const.MANAGE & permission) == Const.MANAGE) {
            permissions.add("MANAGE");
        }
        if ((Const.GRANT & permission) == Const.GRANT) {
            permissions.add("GRANT");
        }
        if ((Const.DOWNLOAD & permission) == Const.DOWNLOAD) {
            permissions.add("DOWNLOAD");
        }
        if ((Const.SHARE & permission) == Const.SHARE) {
            permissions.add("SHARE");
        }
        return permissions;
    }



}

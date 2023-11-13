package org.pipeData.security.manager.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.pipeData.core.entity.User;
import org.pipeData.repository.IUserRepository;
import org.pipeData.security.manager.PermissionDataCache;
import org.pipeData.security.util.JwtUtils;


@Slf4j
public class OpenPipeRealmData extends AuthorizingRealm {

    private final PasswordCredentialsMatcher passwordCredentialsMatcher;

    private final PermissionDataCache permissionDataCache;

    private final IUserRepository userRepository;


    public OpenPipeRealmData(PasswordCredentialsMatcher passwordCredentialsMatcher, PermissionDataCache permissionDataCache, IUserRepository userRepository) {
        this.passwordCredentialsMatcher = passwordCredentialsMatcher;
        this.permissionDataCache = permissionDataCache;
        this.userRepository = userRepository;
    }


    @Override
    public boolean supports(AuthenticationToken token) {
        return true;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = permissionDataCache.getAuthorizationInfo();

        if (authorizationInfo != null) {
            return authorizationInfo;
        }

        String userId = ((User) principals.getPrimaryPrincipal()).getId();

        authorizationInfo = new SimpleAuthorizationInfo();
//        List<Role> userRoles = roleMapper.selectByOrgAndUser(permissionDataCache.getCurrentOrg(), userId);
//        for (Role role : userRoles) {
//            if (role.getType().equals(RoleType.ORG_OWNER.name())) {
//                addOrgOwnerRoleAndPermission(authorizationInfo, role);
//            }
//        }
//        List<RelRoleResource> relRoleResources = rrrMapper.listByOrgAndUser(permissionDataCache.getCurrentOrg(), userId);
//        for (RelRoleResource rrr : relRoleResources) {
//            authorizationInfo.addStringPermissions(ShiroSecurityManager
//                    .toShiroPermissionString(rrr.getOrgId(), rrr.getRoleId(), rrr.getResourceType(), rrr.getResourceId(), rrr.getPermission()));
//        }
        permissionDataCache.setAuthorizationInfo(authorizationInfo);

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SimpleAuthenticationInfo authenticationInfo = permissionDataCache.getAuthenticationInfo();

        if (authenticationInfo != null) {
            return authenticationInfo;
        }

        String username = getUsername(token);
        User user = userRepository.selectByNameOrEmail(username);
        // TODO: 2023/11/8
        if (user == null)
            return null;
        authenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(), getName());
        permissionDataCache.setAuthenticationInfo(authenticationInfo);
        return authenticationInfo;
    }


    @Override
    public CredentialsMatcher getCredentialsMatcher() {
        return passwordCredentialsMatcher;
    }

    private String getUsername(AuthenticationToken token) {
        if (token instanceof BearerToken) {
            return JwtUtils.toJwtToken((String) token.getPrincipal()).getSubject();
        } else {
            return (String) token.getPrincipal();
        }
    }
}

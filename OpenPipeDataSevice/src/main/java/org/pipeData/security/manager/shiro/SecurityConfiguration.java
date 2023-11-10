package org.pipeData.security.manager.shiro;


import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.pipeData.security.manager.PermissionDataCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration {

    @Bean
    public Realm realm(
            PermissionDataCache permissionDataCache,
            PasswordCredentialsMatcher passwordCredentialsMatcher) {

        return new OpenPipeRealmData(passwordCredentialsMatcher, permissionDataCache);
    }

    @Bean
    public SessionsSecurityManager securityManager(Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

}

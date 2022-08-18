package com.springsecurity.rbac.springsecurityrbac.security;

import com.springsecurity.rbac.springsecurityrbac.entity.contsants.PRIVILEGE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Component(value = "roleChecker")
public class JdbcRoleChecker implements RoleChecker {

    private final Logger logger = LoggerFactory.getLogger(JdbcRoleChecker.class);

    private HttpServletRequest request;

    public JdbcRoleChecker(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean check(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || request.getParameter("pageCode") == null) {
            logger.error("Unauthorized user or invalid url access!");
            return false;
        }

        String pageCode = this.request.getParameter("pageCode").toUpperCase();

        //has access to requested page
        if (hasAccessToPage(authentication.getAuthorities(), pageCode)) {

            //has authorization to requested action on requested page
            if (hasAuthorityToActOnPage(authentication.getAuthorities(), pageCode, request.getMethod())) {
                logger.warn("User {} performed {} on {} ", authentication.getName(), request.getMethod(), pageCode);
                return true;
            } else {
                logger.warn("User with id {} tried to perform unauthorized activity {} on page {}",
                        authentication.getName(), request.getMethod(), pageCode);
            }
        } else {
            logger.warn("User with id {} tried to access unauthorized page {}", authentication.getName(), pageCode);
        }

        return false;
    }

    private boolean hasAuthorityToActOnPage(Collection<? extends GrantedAuthority> authorities, String pageCode, String method) {
        String privilege = switch (method.toUpperCase()) {
            case "GET" -> PRIVILEGE.READ;
            case "PUT" -> PRIVILEGE.UPDATE;
            case "POST" -> PRIVILEGE.WRITE;
            case "DELETE" -> PRIVILEGE.DELETE;
            default -> null;
        };

        for (GrantedAuthority authority : authorities) {
            if ((pageCode + "." + privilege).equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    boolean hasAccessToPage(Collection<? extends GrantedAuthority> authorities, String pageCode) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().split("\\.")[0].equals(pageCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //todo: implement
    }
}

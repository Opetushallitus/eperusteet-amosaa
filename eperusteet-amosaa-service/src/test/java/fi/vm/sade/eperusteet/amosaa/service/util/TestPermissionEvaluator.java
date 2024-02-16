package fi.vm.sade.eperusteet.amosaa.service.util;

import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class TestPermissionEvaluator implements org.springframework.security.access.PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return authentication.isAuthenticated();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return authentication.isAuthenticated();
    }
}

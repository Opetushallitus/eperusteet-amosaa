/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePrefix;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * @author jhyoty
 */
public final class SecurityUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtil.class);

    public static final String OPH_OID = "1.2.246.562.10.00000000001";
    public static final String OPH_ADMIN = "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.00000000001";

    private SecurityUtil() {
        //helper class
    }

    public static Principal getAuthenticatedPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void allow(String principalName) {
        Principal p = getAuthenticatedPrincipal();
        if (p == null || !p.getName().equals(principalName)) {
            throw new AccessDeniedException("Pääsy evätty");
        }
    }

    public static Set<String> getOrganizations(Set<RolePermission> permissions, RolePrefix rolePrefix) {
        return getOrganizations(SecurityContextHolder.getContext().getAuthentication(), permissions, rolePrefix);
    }

    public static Set<String> getOrganizations(RolePrefix rolePrefix) {
        return getOrganizations(SecurityContextHolder.getContext().getAuthentication(), rolePrefix);
    }

    public static Set<String> getOrganizations(Authentication authentication, RolePrefix rolePrefix) {
        return getOrganizations(authentication, new HashSet<>(Arrays.asList(RolePermission.values())), rolePrefix);
    }

    public static Set<String> getOrganizations(Authentication authentication, Set<RolePermission> permissions, RolePrefix rolePrefix) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> parseOid(grantedAuthority.getAuthority(),
                        rolePrefix,
                        permissions))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private static Optional<String> parseOid(String authority, RolePrefix prefix, Set<RolePermission> permissions) {
        return permissions.stream()
                .map(p -> {
                    String authPrefix = prefix.name() + "_" + p.name() + "_";
                    return authority.startsWith(authPrefix) ?
                            Optional.of(authority.substring(authPrefix.length())) : Optional.<String>empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    public static boolean isUserOphAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(OPH_ADMIN));
    }
}

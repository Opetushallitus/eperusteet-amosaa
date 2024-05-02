package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePrefix;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

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
                .filter(SecurityUtil::checkOid)
                .findAny();
    }

    public static boolean isUserOphAdmin(PermissionEvaluator.RolePrefix rolePrefix) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(rolePrefixToOphAdmin(rolePrefix)));
    }

    private static String rolePrefixToOphAdmin(PermissionEvaluator.RolePrefix rolePrefix) {
        return rolePrefix.toString() + "_ADMIN_" + OPH_OID;
    }

    public static boolean checkOid(String oid) {
        try {
            new Oid(oid);
        } catch (GSSException e) {
            return false;
        }
        return true;
    }

    public static Authentication useAdminAuth() {
        // Käytetään pääkäyttäjän oikeuksia.
        return new UsernamePasswordAuthenticationToken("system",
                "ROLE_ADMIN", AuthorityUtils.createAuthorityList("ROLE_ADMIN",
                "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.00000000001",
                "ROLE_APP_EPERUSTEET_VST_ADMIN_1.2.246.562.10.00000000001",
                "ROLE_APP_EPERUSTEET_KOTO_ADMIN_1.2.246.562.10.00000000001"));
    }
}

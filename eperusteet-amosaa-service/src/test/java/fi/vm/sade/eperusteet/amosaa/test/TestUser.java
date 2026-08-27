package fi.vm.sade.eperusteet.amosaa.test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;

/**
 * Two-arg {@link UsernamePasswordAuthenticationToken} is unauthenticated in Spring Security 7.
 * Authorities match {@code it-test-context.xml} in-memory users.
 */
public final class TestUser {

    private TestUser() {
    }

    public static UsernamePasswordAuthenticationToken authenticated(String username) {
        return new UsernamePasswordAuthenticationToken(username, "test", authoritiesFor(username));
    }

    private static List<GrantedAuthority> authoritiesFor(String username) {
        if ("oph".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_AMOSAA",
                    "ROLE_APP_EPERUSTEET_VST",
                    "ROLE_APP_EPERUSTEET_TUVA",
                    "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.00000000001",
                    "ROLE_APP_EPERUSTEET_TUVA_ADMIN_1.2.246.562.10.00000000001",
                    "ROLE_APP_EPERUSTEET_VST_ADMIN_1.2.246.562.10.00000000001",
                    "ROLE_APP_EPERUSTEET_KOTO_ADMIN_1.2.246.562.10.00000000001");
        }
        if ("kp1".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_AMOSAA",
                    "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.54645809036");
        }
        if ("kp2".equals(username) || "kp2user2".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_AMOSAA",
                    "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.2013120512391252668625");
        }
        if ("kp3".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_AMOSAA",
                    "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.2013120513110198396408");
        }
        if ("tmpr".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_AMOSAA",
                    "ROLE_APP_EPERUSTEET_AMOSAA_ADMIN_1.2.246.562.10.79499343246");
        }
        if ("tuva".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_TUVA",
                    "ROLE_APP_EPERUSTEET_TUVA_ADMIN_1.2.246.562.10.2013120512391252668625");
        }
        if ("vst".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_VST",
                    "ROLE_APP_EPERUSTEET_VST_ADMIN_1.2.246.562.10.2013120512391252668625");
        }
        if ("koto".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_KOTO",
                    "ROLE_APP_EPERUSTEET_KOTO_ADMIN_1.2.246.562.10.2013120512391252668625");
        }
        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }
}

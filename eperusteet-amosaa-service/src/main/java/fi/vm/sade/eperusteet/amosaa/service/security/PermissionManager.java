package fi.vm.sade.eperusteet.amosaa.service.security;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

public interface PermissionManager {

    boolean hasPermission(Authentication authentication, Serializable targetObject, TargetType target, Permission perm);

    @PreAuthorize("isAuthenticated()")
    Map<PermissionEvaluator.RolePermission, Set<Long>> getOrganisaatioOikeudet(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("isAuthenticated()")
    Map<PermissionEvaluator.RolePermission, Set<Koulutustoimija>> getKoulutustoimijaOikeudet(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("isAuthenticated()")
    boolean hasOphAdminPermission();
}

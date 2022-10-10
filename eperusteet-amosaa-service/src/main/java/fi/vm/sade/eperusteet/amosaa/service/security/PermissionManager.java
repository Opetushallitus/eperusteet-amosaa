package fi.vm.sade.eperusteet.amosaa.service.security;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

public interface PermissionManager {

    boolean hasPermission(Authentication authentication, Serializable targetObject, TargetType target, Permission perm);

    @PreAuthorize("isAuthenticated()")
    Map<PermissionEvaluator.RolePermission, Set<Long>> getOrganisaatioOikeudet(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("isAuthenticated()")
    Map<PermissionEvaluator.RolePermission, Set<KoulutustoimijaBaseDto>> getKoulutustoimijaOikeudet(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("isAuthenticated()")
    boolean hasOphAdminPermission();
}

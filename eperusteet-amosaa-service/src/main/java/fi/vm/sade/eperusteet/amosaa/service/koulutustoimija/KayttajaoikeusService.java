package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("isAuthenticated()")
public interface KayttajaoikeusService {
    List<KayttajaoikeusDto> getKayttooikeudet();

    ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<Long>>> getOrganisaatiooikeudet(PermissionEvaluator.RolePrefix rolePrefix);

    ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<KoulutustoimijaBaseDto>>> getKoulutustoimijaOikeudet(PermissionEvaluator.RolePrefix rolePrefix);
}

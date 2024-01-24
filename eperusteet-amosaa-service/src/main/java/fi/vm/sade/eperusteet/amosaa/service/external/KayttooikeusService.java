package fi.vm.sade.eperusteet.amosaa.service.external;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttooikeusKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface KayttooikeusService {
    
    @PreAuthorize("isAuthenticated()")
    List<KayttooikeusKayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid, PermissionEvaluator.RolePrefix rolePrefix);

}

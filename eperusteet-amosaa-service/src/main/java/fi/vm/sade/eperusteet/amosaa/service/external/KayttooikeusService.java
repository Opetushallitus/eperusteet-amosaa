package fi.vm.sade.eperusteet.amosaa.service.external;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttoikeusKayttajaDto;

public interface KayttooikeusService {
    
    @PreAuthorize("isAuthenticated()")
    List<KayttoikeusKayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid);

}

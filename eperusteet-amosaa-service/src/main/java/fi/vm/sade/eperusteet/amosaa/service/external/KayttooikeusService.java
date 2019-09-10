package fi.vm.sade.eperusteet.amosaa.service.external;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttooikeusKayttajaDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface KayttooikeusService {
    
    @PreAuthorize("isAuthenticated()")
    List<KayttooikeusKayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid);

}

package fi.vm.sade.eperusteet.amosaa.service.external;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface KayttooikeusService {
    
    @PreAuthorize("isAuthenticated()")
    List<KayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid);

}

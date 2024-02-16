package fi.vm.sade.eperusteet.amosaa.service.peruste;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.SuoritustapaLaajaDto;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("isAuthenticated()")
public interface PerusteCacheService {
    PerusteDto get(Long id);

    JsonNode getTutkinnonOsat(Long id);

    SuoritustapaLaajaDto getSuoritustapa(Opetussuunnitelma ops, Long id);

    JsonNode getTutkinnonOsa(Long id, Long tosaId);

    SuoritustapaLaajaDto getSuoritustapa(Long opetussuunnitelmaId, Long perusteId);
}

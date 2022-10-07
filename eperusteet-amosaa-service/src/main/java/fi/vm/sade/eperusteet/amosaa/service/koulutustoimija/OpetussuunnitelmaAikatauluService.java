package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaAikatauluDto;
import java.util.List;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;


public interface OpetussuunnitelmaAikatauluService {

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    List<OpetussuunnitelmaAikatauluDto> save(@P("ktId") Long ktId, @P("opsId") Long opsId, List<OpetussuunnitelmaAikatauluDto> opetussuunnitelmanAikataulut);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<OpetussuunnitelmaAikatauluDto> get(@P("ktId") Long ktId, @P("opsId") Long opsId);
}

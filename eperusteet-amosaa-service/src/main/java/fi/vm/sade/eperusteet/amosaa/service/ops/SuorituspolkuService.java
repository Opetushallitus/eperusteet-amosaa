package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;

import java.util.Set;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SuorituspolkuService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Set<SuorituspolkuRiviDto> getRyhmat(@P("opsId") Long opsId, Long spId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    void setRyhmat(@P("opsId") Long opsId, Long spId, Set<SuorituspolkuRiviDto> rivit);
}

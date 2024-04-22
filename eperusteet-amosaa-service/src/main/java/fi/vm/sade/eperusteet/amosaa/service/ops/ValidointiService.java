package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ValidointiService {
    List<Validointi> validoi(Opetussuunnitelma ops);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<String> tutkinnonOsanValidointivirheet(@P("opsId") Long opsId, Long viiteId);
}

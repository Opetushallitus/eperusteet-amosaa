package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;

import java.util.List;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PoistettuService {
    @PreAuthorize("hasPermission({#ktId, #ops.id}, 'opetussuunnitelma', 'POISTO') or hasPermission(null, 'OPH','HALLINTA')")
    PoistettuDto lisaaPoistettu(@P("ktId") Long koulutustoimija, @P("ops") Opetussuunnitelma ops, SisaltoViite osa);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<PoistettuDto> poistetut(@P("ktId") Long koulutustoimija, @P("opsId") Long opsId);
}

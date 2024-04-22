package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DokumenttiStateService {

    @PreAuthorize("isAuthenticated()")
    Dokumentti save(DokumenttiDto dto);

}

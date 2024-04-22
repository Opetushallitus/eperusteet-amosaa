package fi.vm.sade.eperusteet.amosaa.service.ohje;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.dto.ohje.OhjeDto;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

public interface OhjeService {
    @PreAuthorize("isAuthenticated()")
    List<OhjeDto> getOhjeet(KoulutustyyppiToteutus toteutus);

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    OhjeDto addOhje(OhjeDto dto);

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    OhjeDto editOhje(Long id, OhjeDto dto);

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    public void removeOhje(Long id);
}

package fi.vm.sade.eperusteet.amosaa.service.external;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ArviointiasteikkoService {
    @PreAuthorize("permitAll()")
    List<ArviointiasteikkoDto> getAll();

    @PreAuthorize("permitAll()")
    ArviointiasteikkoDto get(Long id);

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    void update();
}

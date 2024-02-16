package fi.vm.sade.eperusteet.amosaa.service.tilastot;

import fi.vm.sade.eperusteet.amosaa.dto.tilastot.TilastotDto;
import fi.vm.sade.eperusteet.amosaa.dto.tilastot.ToimijaTilastotDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TilastotService {
    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    TilastotDto getTilastot();

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    ToimijaTilastotDto getTilastotToimijakohtaisesti();
}

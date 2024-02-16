package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.LokalisointiDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LokalisointiService {

    @PreAuthorize("permitAll()")
    LokalisointiDto get(String key, String locale);
}

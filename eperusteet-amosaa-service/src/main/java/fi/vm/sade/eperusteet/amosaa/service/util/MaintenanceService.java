package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;

public interface MaintenanceService {

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    void teeJulkaisut(boolean julkaiseKaikki, Set<KoulutusTyyppi> koulutustyypit, OpsTyyppi opsTyyppi);
    
}

package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import java.util.Map;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;

public interface MaintenanceService {

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    void teeJulkaisut(boolean julkaiseKaikki, Set<KoulutusTyyppi> koulutustyypit, OpsTyyppi opsTyyppi);

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    void kaynnistaJob(String job, Map<String, String> parametrit) throws Exception;

    @PreAuthorize("isAuthenticated()")
    void clearCache(String cache);

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    void poistaJulkaisut(Long opsId);

    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    void paivitaKoulutustoimijaOppilaitostyypi();
}

package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@Profile("test")
public class MaintenanceServiceMock implements MaintenanceService {

    @Override
    public void teeJulkaisut(boolean julkaiseKaikki, Set<KoulutusTyyppi> koulutustyypit, OpsTyyppi opsTyyppi) {
    }

    @Override
    public void kaynnistaJob(String job, Map<String, String> parametrit) {
    }

    @Override
    public void clearCache(String cache) {
    }

    @Override
    public void poistaJulkaisut(Long opsId) {

    }

}

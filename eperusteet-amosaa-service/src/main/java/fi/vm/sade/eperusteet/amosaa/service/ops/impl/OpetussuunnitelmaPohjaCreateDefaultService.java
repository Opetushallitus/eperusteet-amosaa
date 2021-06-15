package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaPohjaCreateService;
import java.util.Collections;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class OpetussuunnitelmaPohjaCreateDefaultService implements OpetussuunnitelmaPohjaCreateService {
    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }
}

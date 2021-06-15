package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import java.util.Collections;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class OpetussuunnitelmaCreateDefaultService implements OpetussuunnitelmaCreateService {
    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }
}

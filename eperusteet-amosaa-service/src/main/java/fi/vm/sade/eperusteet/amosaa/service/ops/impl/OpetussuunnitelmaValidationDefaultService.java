package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaPohjaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaValidationService;
import java.util.Collections;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class OpetussuunnitelmaValidationDefaultService implements OpetussuunnitelmaValidationService {
    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }
}

package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import java.util.Set;

public interface OpetussuunnitelmaToteutus {
    Set<KoulutusTyyppi> getTyypit();

    Class getImpl();
}

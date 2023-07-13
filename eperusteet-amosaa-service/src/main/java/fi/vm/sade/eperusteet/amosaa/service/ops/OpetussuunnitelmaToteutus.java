package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;

import java.util.Collections;
import java.util.Set;

public interface OpetussuunnitelmaToteutus {
    Set<KoulutusTyyppi> getTyypit();

    default Set<OpsTyyppi> getOpsTyypit() {
        return Collections.emptySet();
    };

    Class getImpl();
}

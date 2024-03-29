package fi.vm.sade.eperusteet.amosaa.service.peruste.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaSisaltoCreateService;
import fi.vm.sade.eperusteet.amosaa.service.peruste.OpetussuunnitelmaPerustePaivitysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Service
@Transactional
public class DefaultOpetussuunnitelmaPerustePaivitysService implements OpetussuunnitelmaPerustePaivitysService {

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }

}

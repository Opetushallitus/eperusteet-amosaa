package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.Koulutuskoodi;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.KoulutuskoodiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@StepScope
public class KoulutuskoodiConvertProcessor implements ItemProcessor<Long, CachedPeruste> {

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    private KoulutuskoodiRepository koulutuskoodiRepository;

    @Override
    public CachedPeruste process(Long cachedPerusteId) throws Exception {
        CachedPeruste peruste = cachedPerusteRepository.findOne(cachedPerusteId);
        peruste.setKoulutuskoodit(peruste.getKoulutukset().stream().map(koulutusDto ->  koulutuskoodiRepository.save(Koulutuskoodi.of(koulutusDto))).collect(Collectors.toSet()));
        return peruste;
    }
}

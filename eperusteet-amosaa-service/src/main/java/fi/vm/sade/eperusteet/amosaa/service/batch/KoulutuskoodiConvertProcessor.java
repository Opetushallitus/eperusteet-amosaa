package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@StepScope
public class KoulutuskoodiConvertProcessor implements ItemProcessor<Long, CachedPeruste> {

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Override
    public CachedPeruste process(Long cachedPerusteId) throws Exception {
        CachedPeruste peruste = cachedPerusteRepository.findOne(cachedPerusteId);
        peruste.setKoulutuskooditFromKoulutusDto(peruste.getKoulutukset());
        return peruste;
    }
}

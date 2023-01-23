package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@StepScope
public class KoulutuskoodiConvertProcessor implements ItemProcessor<Long, CachedPeruste> {
    @Override
    public CachedPeruste process(Long aLong) throws Exception {
        return null;
    }
}

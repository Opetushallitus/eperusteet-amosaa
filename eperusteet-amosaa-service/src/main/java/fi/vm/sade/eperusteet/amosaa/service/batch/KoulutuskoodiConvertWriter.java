package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@StepScope
public class KoulutuskoodiConvertWriter implements ItemWriter<CachedPeruste> {

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Override
    public void write(Chunk<? extends CachedPeruste> list) throws Exception {
        log.info("writing {} peruste", list.size());
        list.forEach(peruste -> {
            cachedPerusteRepository.save(peruste);
        });
    }
}

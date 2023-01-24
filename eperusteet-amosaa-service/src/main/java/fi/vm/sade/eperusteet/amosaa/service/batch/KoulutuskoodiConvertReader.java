package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@StepScope
public class KoulutuskoodiConvertReader implements ItemReader<Long> {

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    private List<Long> ids = null;

    @Override
    public Long read() throws Exception {

        if (ids == null) {
            log.info("KoulutuskoodiConvertReader alkudata fetch");
            ids = cachedPerusteRepository.findByKoulutuksetNotNull();
            log.info("ids count {}", ids.size());
        }

        if (!ids.isEmpty()) {
            return ids.remove(ids.size() - 1);
        }

        ids = null;
        log.info("all read");
        return null;
    }
}

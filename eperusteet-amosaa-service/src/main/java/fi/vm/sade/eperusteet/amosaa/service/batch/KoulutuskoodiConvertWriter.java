package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@StepScope
public class KoulutuskoodiConvertWriter implements ItemWriter<Julkaisu> {
    @Override
    public void write(List<? extends Julkaisu> list) throws Exception {

    }
}

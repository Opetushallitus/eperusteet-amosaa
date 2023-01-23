package fi.vm.sade.eperusteet.amosaa.service.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@StepScope
public class KoulutuskoodiConvertReader implements ItemReader<Long> {
    @Override
    public Long read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }
}

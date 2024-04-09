package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceJulkaisuTarkistus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@StepScope
public class JulkaisuJobReader implements ItemReader<Long> {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private List<Long> ids = null;

    @Value("#{jobParameters['julkaisutarkistus']}")
    private String julkaisutarkistus;

    @Value("#{jobParameters['opsTyyppi']}")
    private String opsTyyppi;

    @Value("#{jobParameters['koulutustyypit']}")
    private String koulutustyypit;

    @Override
    public Long read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (ids == null) {
            log.info("JulkaisuJobItemReader alkudata fetch");
            fetchData();
        }

        if (!ids.isEmpty()) {
            return ids.remove(ids.size() - 1);
        }

        ids = null;
        return null;
    }

    private void fetchData() {
        List<Opetussuunnitelma> opetussuunnitelmat;
        if (koulutustyypit != null) {
            opetussuunnitelmat = opetussuunnitelmaRepository
                    .findByTyyppiAndKoulutustyyppi(OpsTyyppi.of(opsTyyppi), Arrays.stream(koulutustyypit.split(",")).map(KoulutusTyyppi::of).collect(Collectors.toSet()));
        } else {
            opetussuunnitelmat = opetussuunnitelmaRepository.findByTyyppi(OpsTyyppi.of(opsTyyppi));
        }

        MaintenanceJulkaisuTarkistus maintenanceJulkaisuTarkistus = MaintenanceJulkaisuTarkistus.valueOf(julkaisutarkistus.toUpperCase());

        ids = opetussuunnitelmat.stream()
                .filter(peruste -> maintenanceJulkaisuTarkistus.equals(MaintenanceJulkaisuTarkistus.KAIKKI)
                        || (CollectionUtils.isEmpty(peruste.getJulkaisut()) && maintenanceJulkaisuTarkistus.equals(MaintenanceJulkaisuTarkistus.JULKAISEMATTOMAT))
                        || (!CollectionUtils.isEmpty(peruste.getJulkaisut()) && maintenanceJulkaisuTarkistus.equals(MaintenanceJulkaisuTarkistus.JULKAISTUT)))
                .map(Opetussuunnitelma::getId)
                .collect(Collectors.toList());
    }
}

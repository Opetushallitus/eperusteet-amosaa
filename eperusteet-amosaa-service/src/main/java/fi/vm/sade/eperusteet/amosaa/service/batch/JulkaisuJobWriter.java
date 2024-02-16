package fi.vm.sade.eperusteet.amosaa.service.batch;

import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@StepScope
public class JulkaisuJobWriter implements ItemWriter<Julkaisu> {

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private OpetussuunnitelmaMuokkaustietoService opetussuunnitelmaMuokkaustietoService;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    public void write(List<? extends Julkaisu> julkaisut) throws Exception {
        for (Julkaisu julkaisu : julkaisut) {
            log.info("Luodaan julkaisu opetussuunnitelmalle: " + julkaisu.getOpetussuunnitelma().getId());
            julkaisuRepository.save(julkaisu);

            Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(julkaisu.getOpetussuunnitelma().getId());
            opetussuunnitelmaMuokkaustietoService.addOpsMuokkausTieto(
                    julkaisu.getOpetussuunnitelma().getId(),
                    ops,
                    MuokkausTapahtuma.JULKAISU,
                    julkaisu.getOpetussuunnitelma().getNavigationType(),
                    null,
                    "maintenance");
        }
    }
}

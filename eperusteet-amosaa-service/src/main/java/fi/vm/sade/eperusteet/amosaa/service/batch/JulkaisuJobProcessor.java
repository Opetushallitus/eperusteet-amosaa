package fi.vm.sade.eperusteet.amosaa.service.batch;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuData;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.util.JsonMapper;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@StepScope
public class JulkaisuJobProcessor implements ItemProcessor<Long, Julkaisu> {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private OpetussuunnitelmaMuokkaustietoService opetussuunnitelmaMuokkaustietoService;

    @Override
    public Julkaisu process(Long opsId) throws Exception {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
        List<Julkaisu> vanhatJulkaisut = julkaisuRepository.findAllByOpetussuunnitelma(opetussuunnitelma);

        OpetussuunnitelmaKaikkiDto sisalto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opetussuunnitelma.getKoulutustoimija().getId(), opetussuunnitelma.getId());
        Julkaisu julkaisu = new Julkaisu();
        julkaisu.setRevision(vanhatJulkaisut.stream().mapToInt(Julkaisu::getRevision).max().orElse(0) + 1);
        julkaisu.setLuoja("maintenance");
        julkaisu.setTiedote(LokalisoituTeksti.of(Kieli.FI, "Ylläpidon suorittama julkaisu"));
        julkaisu.setLuotu(new Date());
        julkaisu.setOpetussuunnitelma(opetussuunnitelma);

        ObjectNode dataJson = null;
        dataJson = (ObjectNode) jsonMapper.toJson(sisalto);
        julkaisu.setData(new JulkaisuData(dataJson));

        return julkaisu;
    }
}

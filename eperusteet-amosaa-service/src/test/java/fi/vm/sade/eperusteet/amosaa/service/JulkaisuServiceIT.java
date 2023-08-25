package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.test.AbstractDockerIntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
public class JulkaisuServiceIT extends AbstractDockerIntegrationTest {

    @Autowired
    private JulkaisuService julkaisuService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private DtoMapper mapper;

    @BeforeClass
    public static void setup() {
        System.setProperty("fi.vm.sade.eperusteet.salli_virheelliset", "true");
    }

    @Test
    public void testJulkaisu() throws ExecutionException, InterruptedException {
        useProfileOPH();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();

        assertThat(julkaisuService.getJulkaisut(getKoulutustoimijaId(), ops.getId())).isEmpty();

        CompletableFuture<Void> asyncResult = julkaisuService.teeJulkaisu(ops.getKoulutustoimija().getId(), ops.getId(), createJulkaisu(ops));
//        asyncResult.get();

    }

    private JulkaisuBaseDto createJulkaisu(OpetussuunnitelmaBaseDto ops) {
        JulkaisuBaseDto julkaisu = new JulkaisuBaseDto();
        julkaisu.setTiedote(LokalisoituTekstiDto.of(Kieli.FI, "Tiedote"));
        julkaisu.setLuoja("test");
        julkaisu.setLuotu(new Date());
        julkaisu.setOpetussuunnitelma(ops);
        return julkaisu;
    }

    private Julkaisu getJulkaisu(OpetussuunnitelmaBaseDto ops, Integer revision) {
        return julkaisuRepository.findByOpetussuunnitelmaAndRevision(mapper.map(ops, Opetussuunnitelma.class), revision);
    }

    private List<Julkaisu> getJulkaisut(OpetussuunnitelmaBaseDto ops) {
        return julkaisuRepository.findAllByOpetussuunnitelma(mapper.map(ops, Opetussuunnitelma.class));
    }
}

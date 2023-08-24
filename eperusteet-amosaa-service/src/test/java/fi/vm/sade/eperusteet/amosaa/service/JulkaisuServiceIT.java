package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractDockerIntegrationTest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
public class JulkaisuServiceIT extends AbstractDockerIntegrationTest {

    @Autowired
    private JulkaisuService julkaisuService;

    @BeforeClass
    public static void setup() {
        System.setProperty("fi.vm.sade.eperusteet.salli_virheelliset", "true");
    }

    @Test
    @Ignore
    public void testJulkaisu1() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        assertThat(julkaisuService.getJulkaisut(getKoulutustoimijaId(), ops.getId())).isEmpty();

        JulkaisuBaseDto julkaisuDto = JulkaisuBaseDto.builder().tiedote(LokalisoituTekstiDto.of("tiedote")).build();
//        JulkaisuBaseDto uusiJulkaisu = julkaisuService.teeJulkaisu(getKoulutustoimijaId(), ops.getId(), julkaisuDto);
//
//        assertThat(uusiJulkaisu.getLuoja()).isNotNull();
//        assertThat(uusiJulkaisu.getLuotu()).isNotNull();
//        assertThat(uusiJulkaisu.getRevision()).isEqualTo(1);
//        assertThat(uusiJulkaisu.getOpetussuunnitelma().getId()).isEqualTo(ops.getId());
//        assertThat(uusiJulkaisu.getTiedote().get(Kieli.FI)).isEqualTo("tiedote");

        // FIXME: haun kutsu kaatuu koska julkaisu_data taulua ei ole enaa olemassa???
        //assertThat(julkaisuService.getJulkaisut(getKoulutustoimijaId(), this.opsId)).hasSize(1);
    }
}

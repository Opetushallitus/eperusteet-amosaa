package fi.vm.sade.eperusteet.amosaa.service.validation;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.LaajuusYksikko;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaValidationService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.impl.VstOpetussuunnitelmaValidationService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class VstOpetussuunnitelmaValidationServiceTest extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaValidationService vstOpetussuunnitelmaValidationService;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    OpetussuunnitelmaBaseDto vstOps;

    SisaltoViiteDto.Matala root;

    @Before
    public void init() {
        useProfileVst();
        vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(35820L));
        root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), vstOps.getId());
    }

    @Test
    public void testSisallostaPuuttuuOpintokokonaisuusNimi() {
        SisaltoViiteDto.Matala opintokokonaisuus1 = createOpintokokonaisuus();
        SisaltoViiteDto.Matala opintokokonaisuus2 = createOpintokokonaisuus();

        Validointi validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet().stream()
                .filter(virhe -> virhe.getKuvaus().equals(VstOpetussuunnitelmaValidationService.SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA)).collect(Collectors.toList())).hasSize(1);

        opintokokonaisuus1.getTekstiKappale().setNimi(LokalisoituTekstiDto.of("nimi1"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), opintokokonaisuus1.getId(), opintokokonaisuus1);
        validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet().stream()
                .filter(virhe -> virhe.getKuvaus().equals(VstOpetussuunnitelmaValidationService.SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA)).collect(Collectors.toList())).hasSize(1);

        opintokokonaisuus2.getTekstiKappale().setNimi(LokalisoituTekstiDto.of("nimi2"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), opintokokonaisuus2.getId(), opintokokonaisuus2);
        validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet().stream().noneMatch(virhe -> virhe.getKuvaus().equals(VstOpetussuunnitelmaValidationService.SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA))).isTrue();
    }

    @Test
    public void testSisallostaPuuttuuOpintokokonaisuusLaajuusJaYksikko() {
        // validoidaan vstOps:n defaultina luotu opintokokonaisuus
        Validointi validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet()).extracting(Validointi.Virhe::getKuvaus)
                .doesNotContain("sisallossa-opintokokonaisuuksia-ilman-laajuutta")
                .doesNotContain("sisallossa-opintokokonaisuuksia-ilman-laajuusyksikkoa");
        assertThat(validointi.getVaroitukset()).extracting(Validointi.Virhe::getKuvaus)
                .contains("sisallossa-opintokokonaisuuksia-ilman-laajuutta");

        SisaltoViiteDto viite = sisaltoViiteService.getSisaltoviitteet(getKoulutustoimijaId(), vstOps.getId(), SisaltoTyyppi.OPINTOKOKONAISUUS).get(0);

        viite.getOpintokokonaisuus().setLaajuus(BigDecimal.valueOf(30.0));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), viite.getId(), viite);
        validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet()).extracting(Validointi.Virhe::getKuvaus)
                .doesNotContain("sisallossa-opintokokonaisuuksia-ilman-laajuutta")
                .contains("sisallossa-opintokokonaisuuksia-ilman-laajuusyksikkoa");

        viite.getOpintokokonaisuus().setLaajuusYksikko(LaajuusYksikko.OPINTOPISTE);
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), viite.getId(), viite);
        validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet()).extracting(Validointi.Virhe::getKuvaus)
                .doesNotContain("sisallossa-opintokokonaisuuksia-ilman-laajuutta")
                .doesNotContain("sisallossa-opintokokonaisuuksia-ilman-laajuusyksikkoa");
    }

    private SisaltoViiteDto.Matala createOpintokokonaisuus() {
        return sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), root.getId(),
                createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.OPINTOKOKONAISUUS);
                    sisaltoViiteDto.setTekstiKappale(new TekstiKappaleDto());
                    sisaltoViiteDto.setOpintokokonaisuus(new OpintokokonaisuusDto());
                }));
    }
}

package fi.vm.sade.eperusteet.amosaa.service.validation;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaValidationService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.impl.VstOpetussuunnitelmaValidationService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class VstOpetussuunnitelmaValidationServiceTest extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaValidationService vstOpetussuunnitelmaValidationService;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Test
    public void testSisallostaPuuttuuOpintokokonaisuusNimi() {
        useProfileVst();
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(35820L));
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), vstOps.getId());
        SisaltoViiteDto.Matala opintokokonaisuus1 = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), root.getId(),
                createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.OPINTOKOKONAISUUS);
                    sisaltoViiteDto.setTekstiKappale(new TekstiKappaleDto());
                    sisaltoViiteDto.setOpintokokonaisuus(new OpintokokonaisuusDto());
                }));
        SisaltoViiteDto.Matala opintokokonaisuus2 = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), root.getId(),
                createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.OPINTOKOKONAISUUS);
                    sisaltoViiteDto.setTekstiKappale(new TekstiKappaleDto());
                    sisaltoViiteDto.setOpintokokonaisuus(new OpintokokonaisuusDto());
                }));

        Validointi validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet().stream()
                .filter(virhe -> virhe.getSyy().equals(VstOpetussuunnitelmaValidationService.SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA)).collect(Collectors.toList())).hasSize(1);

        opintokokonaisuus1.getTekstiKappale().setNimi(LokalisoituTekstiDto.of("nimi1"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), opintokokonaisuus1.getId(), opintokokonaisuus1);
        validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet().stream()
                .filter(virhe -> virhe.getSyy().equals(VstOpetussuunnitelmaValidationService.SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA)).collect(Collectors.toList())).hasSize(1);

        opintokokonaisuus2.getTekstiKappale().setNimi(LokalisoituTekstiDto.of("nimi2"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), vstOps.getId(), opintokokonaisuus2.getId(), opintokokonaisuus2);
        validointi = vstOpetussuunnitelmaValidationService.validoi(getKoulutustoimijaId(), vstOps.getId());
        assertThat(validointi.getVirheet().stream().noneMatch(virhe -> virhe.getSyy().equals(VstOpetussuunnitelmaValidationService.SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA))).isTrue();

    }
}

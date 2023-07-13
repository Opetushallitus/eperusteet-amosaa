package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaDispatcher;
import fi.vm.sade.eperusteet.amosaa.service.peruste.OpetussuunnitelmaPerustePaivitysService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class KoulutustoimijaYhteinenOsuusCreateServiceIT extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TutkinnonosaRepository tutkinnonosaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private EperusteetClient eperusteetClient;

    @Test
    public void testOpetussuunnitelmaOsaALuePoistettu() {

        // 1: oph-pohja create
        useProfileOPH();
        OpetussuunnitelmaBaseDto ophPohjaDto = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(null);
            opsLuonti.setTyyppi(OpsTyyppi.POHJA);
        });
        Opetussuunnitelma ophPohja = opetussuunnitelmaRepository.findOne(ophPohjaDto.getId());

        assertThat(ophPohja.getTyyppi()).isEqualTo(OpsTyyppi.POHJA);
        assertThat(ophPohja.getKoulutustoimija().getOrganisaatio()).isEqualTo(AbstractIntegrationTest.oidOph);

        SisaltoViiteDto.Matala pohjaRoot = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ophPohja.getId());
        sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ophPohja.getId(), pohjaRoot.getId(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
            sisaltoViiteDto.setTekstiKappale(new TekstiKappaleDto(LokalisoituTekstiDto.of("otsikko1"), LokalisoituTekstiDto.of("teksti1"), null));
        }));

        // 2: koulutustoimija yhteinen osa (pohja) create
        useProfileKP1();
        OpetussuunnitelmaBaseDto opsDto1 = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(null);
            opsLuonti.setTyyppi(OpsTyyppi.YHTEINEN);
            opsLuonti.setOpsId(ophPohja.getId());
        });

        Opetussuunnitelma ops1 = opetussuunnitelmaRepository.findOne(opsDto1.getId());
        assertThat(ops1.getPohja()).isNotNull();
        assertThat(ops1.getPohja().getId()).isEqualTo(ophPohja.getId());

        SisaltoViiteDto.Matala ops1root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops1.getId());
        SisaltoViiteDto.Matala ops1Tekstikappale = sisaltoViiteService.getSisaltoViite(getKoulutustoimijaId(), ops1.getId(), ops1root.getLapset().get(0).getIdLong());
        assertThat(ops1Tekstikappale.getPohjanTekstikappale()).isNull();
        assertThat(ops1Tekstikappale.getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("otsikko1");
        assertThat(ops1Tekstikappale.getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("teksti1");

        // 3: koulutustoimija yhteinen osa create
        OpetussuunnitelmaBaseDto opsDto2 = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(null);
            opsLuonti.setTyyppi(OpsTyyppi.YHTEINEN);
            opsLuonti.setOpsId(ops1.getId());
        });

        Opetussuunnitelma ops2 = opetussuunnitelmaRepository.findOne(opsDto2.getId());
        assertThat(ops2.getPohja()).isNotNull();
        assertThat(ops2.getPohja().getId()).isEqualTo(ops1.getId());

        SisaltoViiteDto.Matala ops2root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops2.getId());
        SisaltoViiteDto.Matala ops2Tekstikappale = sisaltoViiteService.getSisaltoViite(getKoulutustoimijaId(), ops2.getId(), ops2root.getLapset().get(0).getIdLong());
        assertThat(ops2Tekstikappale.getPohjanTekstikappale()).isNotNull();
        assertThat(ops2Tekstikappale.getPohjanTekstikappale().getNimi().get(Kieli.FI)).isEqualTo("otsikko1");
        assertThat(ops2Tekstikappale.getPohjanTekstikappale().getTeksti().get(Kieli.FI)).isEqualTo("teksti1");
        assertThat(ops2Tekstikappale.getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("otsikko1");
        assertThat(ops2Tekstikappale.getTekstiKappale().getTeksti()).isNull();

        ops2Tekstikappale.getTekstiKappale().setTeksti(LokalisoituTekstiDto.of("teksti2"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), ops2.getId(), ops2root.getLapset().get(0).getIdLong(), ops2Tekstikappale);

        // 4: koulutustoimija yhteinen osa create - pohjana yhteinen osa
        OpetussuunnitelmaBaseDto opsDto3 = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(null);
            opsLuonti.setTyyppi(OpsTyyppi.YHTEINEN);
            opsLuonti.setOpsId(ops2.getId());
        });

        Opetussuunnitelma ops3 = opetussuunnitelmaRepository.findOne(opsDto3.getId());
        assertThat(ops3.getPohja()).isNotNull();
        assertThat(ops3.getPohja().getId()).isEqualTo(ops1.getId());

        SisaltoViiteDto.Matala ops3root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops3.getId());
        SisaltoViiteDto.Matala ops3Tekstikappale = sisaltoViiteService.getSisaltoViite(getKoulutustoimijaId(), ops3.getId(), ops3root.getLapset().get(0).getIdLong());
        assertThat(ops3Tekstikappale.getPohjanTekstikappale()).isNotNull();
        assertThat(ops3Tekstikappale.getPohjanTekstikappale().getNimi().get(Kieli.FI)).isEqualTo("otsikko1");
        assertThat(ops3Tekstikappale.getPohjanTekstikappale().getTeksti().get(Kieli.FI)).isEqualTo("teksti1");
        assertThat(ops3Tekstikappale.getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("otsikko1");
        assertThat(ops3Tekstikappale.getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("teksti2");
    }
}

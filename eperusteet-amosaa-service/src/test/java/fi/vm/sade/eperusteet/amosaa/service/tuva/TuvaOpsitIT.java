package fi.vm.sade.eperusteet.amosaa.service.tuva;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusOsanKoulutustyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusOsanTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KoulutuksenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class TuvaOpsitIT extends AbstractIntegrationTest {

    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Test
    @Rollback
    public void test_createTuvaPohja() {
        useProfileOPH();
        OpetussuunnitelmaBaseDto pohjaOps = createOpetussuunnitelma(ops -> {
            ops.setPerusteId(76091l);
            ops.setTyyppi(OpsTyyppi.OPSPOHJA);
            ops.setKoulutustyyppi(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
        });
        List<SisaltoviiteLaajaDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(pohjaOps.getKoulutustoimija().getId(), pohjaOps.getId(), SisaltoviiteLaajaDto.class);

        assertThat(sisaltoviitteet).hasSize(9);

        checkSisaltoviiteSisallot(sisaltoviitteet);
    }


    @Test
    public void test_createTuvaOps() {
        useProfileOPH();
        OpetussuunnitelmaBaseDto pohjaOps = createOpetussuunnitelma(ops -> {
            ops.setPerusteId(76091l);
            ops.setTyyppi(OpsTyyppi.OPSPOHJA);
            ops.setKoulutustyyppi(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
        });

        useProfileKP1();
        OpetussuunnitelmaBaseDto tuvaOps = createOpetussuunnitelma(ops -> {
            ops.setPerusteId(null);
            ops.setTyyppi(OpsTyyppi.OPS);
            ops.setOpsId(pohjaOps.getId());
            ops.setKoulutustyyppi(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
        });

        List<SisaltoviiteLaajaDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(tuvaOps.getKoulutustoimija().getId(), tuvaOps.getId(), SisaltoviiteLaajaDto.class);

        assertThat(sisaltoviitteet).hasSize(10);
        List<SisaltoviiteLaajaDto> koulutuksenosat = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOULUTUKSENOSAT.equals(viite.getTyyppi())).collect(Collectors.toList());
        assertThat(koulutuksenosat).hasSize(1);
        SisaltoViiteDto.Matala koulutuksenosatViite = sisaltoViiteService.getSisaltoViite(tuvaOps.getKoulutustoimija().getId(), tuvaOps.getId(), koulutuksenosat.get(0).getId());
        assertThat(koulutuksenosatViite.getLapset()).hasSize(6);

        checkSisaltoviiteSisallot(sisaltoviitteet);
    }

    private void checkSisaltoviiteSisallot(List<SisaltoviiteLaajaDto> sisaltoviitteet) {
        List<SisaltoviiteLaajaDto> tekstikappaleet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());
        List<SisaltoviiteLaajaDto> koulutuksenosat = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.KOULUTUKSENOSA.equals(viite.getTyyppi())).collect(Collectors.toList());
        List<SisaltoviiteLaajaDto> laajaalaisetosaamiset = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.LAAJAALAINENOSAAMINEN.equals(viite.getTyyppi())).collect(Collectors.toList());

        assertThat(tekstikappaleet).hasSize(1);
        assertThat(koulutuksenosat).hasSize(6);
        assertThat(laajaalaisetosaamiset).hasSize(1);

        assertThat(tekstikappaleet).extracting("tekstiKappale.nimi.tekstit")
                .containsExactlyInAnyOrder(
                        LokalisoituTekstiDto.of("tekstikappale11").getTeksti());

        assertThat(laajaalaisetosaamiset).extracting("tuvaLaajaAlainenOsaaminen.nimiKoodi")
                .containsExactlyInAnyOrder(
                        "tutkintokoulutukseenvalmentavakoulutuslaajaalainenosaaminen_005");
        assertThat(koulutuksenosat).extracting("koulutuksenosa.nimi.tekstit")
                .containsExactlyInAnyOrder(
                        null,
                        LokalisoituTekstiDto.of("Henkilökohtainen opiskelusuunnitelma").getTeksti(),
                        LokalisoituTekstiDto.of("Erityinen tuki").getTeksti(),
                        LokalisoituTekstiDto.of("opiskeluhuolto").getTeksti(),
                        LokalisoituTekstiDto.of("koulutuksen järjestäjän suunnitelmaa").getTeksti(),
                        null);

        assertThat(koulutuksenosat).extracting("koulutuksenosa.nimiKoodi")
                .containsExactlyInAnyOrder(
                        "koulutuksenosattuva_101",
                        null,
                        null,
                        null,
                        null,
                        "koulutuksenosattuva_104");


        KoulutuksenOsaDto koulutuksenosa = koulutuksenosat.get(0).getKoulutuksenosa();
        assertThat(koulutuksenosa.getKuvaus().get(Kieli.FI)).isEqualTo(LokalisoituTekstiDto.of("<p>kuvausteksti</p>").get(Kieli.FI));
        assertThat(koulutuksenosa.getLaajuusMinimi()).isEqualTo(1);
        assertThat(koulutuksenosa.getLaajuusMaksimi()).isEqualTo(5);
        assertThat(koulutuksenosa.getKoulutusOsanKoulutustyyppi()).isEqualTo(KoulutusOsanKoulutustyyppi.TUTKINTOKOULUTUKSEENVALMENTAVA);
        assertThat(koulutuksenosa.getKoulutusOsanTyyppi()).isEqualTo(KoulutusOsanTyyppi.YHTEINEN);
        assertThat(koulutuksenosa.getTavoitteet()).hasSize(1);
        assertThat(koulutuksenosa.getTavoitteet()).extracting("tekstit")
                .containsExactlyInAnyOrder(
                        LokalisoituTekstiDto.of("tavoite1").getTeksti());
        assertThat(koulutuksenosa.getLaajaAlaisenOsaamisenKuvaus().get(Kieli.FI)).isEqualTo(LokalisoituTekstiDto.of("<p>laaja</p>").get(Kieli.FI));
        assertThat(koulutuksenosa.getKeskeinenSisalto().get(Kieli.FI)).isEqualTo(LokalisoituTekstiDto.of("<p>keski</p>").get(Kieli.FI));
        assertThat(koulutuksenosa.getArvioinninKuvaus().get(Kieli.FI)).isEqualTo(LokalisoituTekstiDto.of("<p>osa</p>").get(Kieli.FI));
    }
}

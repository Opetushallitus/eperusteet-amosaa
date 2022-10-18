package fi.vm.sade.eperusteet.amosaa.service.vst;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTavoite;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoUriArvo;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoviiteServiceProvider;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DirtiesContext
@Transactional
@Slf4j
public class VapaasivistystyoOpsIT extends AbstractIntegrationTest {

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private SisaltoviiteServiceProvider sisaltoviiteServiceProvider;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Before
    public void setup(){
        useProfileVst();
    }

    @Test
    @Rollback
    public void test_vapaasivistystyoSisaltoviitteet() {
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(35820L));
        List<SisaltoviiteLaajaDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class);

        assertThat(sisaltoviitteet).hasSize(3);

        List<SisaltoviiteLaajaDto> tekstikappaleet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());
        List<SisaltoviiteLaajaDto> opintokokonaisuudet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.OPINTOKOKONAISUUS.equals(viite.getTyyppi())).collect(Collectors.toList());

        assertThat(tekstikappaleet).hasSize(1);
        assertThat(opintokokonaisuudet).hasSize(1);

        assertThat(tekstikappaleet).extracting("tekstiKappale.nimi.tekstit")
                .containsExactlyInAnyOrder(
                        LokalisoituTekstiDto.of("tekstikappale 1").getTeksti());

        assertThat(opintokokonaisuudet).extracting("tekstiKappale.nimi.tekstit")
                .containsExactlyInAnyOrder(
                        LokalisoituTekstiDto.of("Opiskelu- ja urasuunnittelutaidot").getTeksti());
        OpintokokonaisuusDto opintokokonaisuus = opintokokonaisuudet.get(0).getOpintokokonaisuus();
        assertThat(opintokokonaisuus.getKuvaus().get(Kieli.FI)).isEqualTo(LokalisoituTekstiDto.of("<p>opintokokonaisuus kuvaus x</p>").get(Kieli.FI));
        assertThat(opintokokonaisuus.getArvioinninKuvaus()).isNull();
        assertThat(opintokokonaisuus.getKeskeisetSisallot()).isNull();
        assertThat(opintokokonaisuus.getOpetuksenTavoiteOtsikko().get(Kieli.FI)).isEqualTo(LokalisoituTekstiDto.of("tavoitteita xx").get(Kieli.FI));
        assertThat(opintokokonaisuus.getMinimilaajuus()).isEqualTo(5);
        assertThat(opintokokonaisuus.getArvioinnit()).hasSize(3);
        assertThat(opintokokonaisuus.getArvioinnit()).extracting("arviointi.tekstit")
                .containsExactlyInAnyOrder(
                        LokalisoituTekstiDto.of("arviointi 1").getTeksti(),
                        LokalisoituTekstiDto.of("arviointi 2").getTeksti(),
                        LokalisoituTekstiDto.of("arviointi 3").getTeksti()
                );
        assertThat(opintokokonaisuus.getTavoitteet()).hasSize(3);
        assertThat(opintokokonaisuus.getTavoitteet()).extracting("tavoiteKoodi")
                .containsExactlyInAnyOrder(
                        "opintokokonaisuustavoitteet_1046",
                        "opintokokonaisuustavoitteet_1047",
                        "opintokokonaisuustavoitteet_1048");
        assertThat(opintokokonaisuus.getTavoitteet()).extracting("tavoite")
                .containsExactlyInAnyOrder(
                        null,
                        null,
                        null);
    }

    @Test
    public void test_vapaasivistystyoSisaltoviitteet_lisaaTavoitteita() {
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(35820L));
        List<SisaltoviiteLaajaDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class);
        List<SisaltoviiteLaajaDto> opintokokonaisuudet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.OPINTOKOKONAISUUS.equals(viite.getTyyppi())).collect(Collectors.toList());
        OpintokokonaisuusDto opintokokonaisuus = opintokokonaisuudet.get(0).getOpintokokonaisuus();

        SisaltoViiteDto.Matala sisaltoviiteDto = sisaltoViiteService.getSisaltoViite(vstOps.getKoulutustoimija().getId(), vstOps.getId(), opintokokonaisuudet.get(0).getId());

        sisaltoviiteDto.getOpintokokonaisuus().setTavoitteet(Stream.concat(
                opintokokonaisuus.getTavoitteet().stream(),
                Stream.of(OpintokokonaisuusTavoiteDto.builder()
                        .perusteesta(false)
                        .tavoite(LokalisoituTekstiDto.of("tavoite1"))
                        .build()
                )).collect(Collectors.toList()));

        sisaltoViiteService.updateSisaltoViite(vstOps.getKoulutustoimija().getId(), vstOps.getId(), opintokokonaisuudet.get(0).getId(), sisaltoviiteDto);

        sisaltoviiteDto = sisaltoViiteService.getSisaltoViite(vstOps.getKoulutustoimija().getId(), vstOps.getId(), opintokokonaisuudet.get(0).getId());
        assertThat(sisaltoviiteDto.getOpintokokonaisuus().getTavoitteet()).extracting("tavoiteKoodi")
                .containsExactlyInAnyOrder(
                        "opintokokonaisuustavoitteet_1046",
                        "opintokokonaisuustavoitteet_1047",
                        "opintokokonaisuustavoitteet_1048",
                        null);
        assertThat(sisaltoviiteDto.getOpintokokonaisuus().getTavoitteet().get(3))
                .extracting("tavoite")
                .extracting("tekstit")
                .containsExactlyInAnyOrder(LokalisoituTekstiDto.of("tavoite1").getTeksti());

        SisaltoViite sisaltoViite = sisaltoviiteRepository.getOne(sisaltoviiteDto.getId());
        sisaltoviiteServiceProvider.koodita(sisaltoViite);
        sisaltoviiteDto = sisaltoViiteService.getSisaltoViite(vstOps.getKoulutustoimija().getId(), vstOps.getId(), opintokokonaisuudet.get(0).getId());
        assertThat(sisaltoviiteDto.getOpintokokonaisuus().getTavoitteet()).extracting("tavoiteKoodi").doesNotContainNull();
    }

    @Test
    public void test_vapaasivistystyoSisaltoviitteet_opintokokonaisuus_kooditus() {
        OpetussuunnitelmaBaseDto vstOps = createOpetussuunnitelma(ops -> ops.setPerusteId(35820L));
        List<SisaltoviiteLaajaDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(vstOps.getKoulutustoimija().getId(), vstOps.getId(), SisaltoviiteLaajaDto.class);
        List<SisaltoviiteLaajaDto> opintokokonaisuudet = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.OPINTOKOKONAISUUS.equals(viite.getTyyppi())).collect(Collectors.toList());

        opintokokonaisuudet.forEach(ok -> {
            assertThat(ok.getOpintokokonaisuus().getKoodi()).isNull();
            SisaltoViite sisaltoViite = sisaltoviiteRepository.getOne(ok.getId());
            sisaltoviiteServiceProvider.koodita(sisaltoViite);
            sisaltoViite = sisaltoviiteRepository.getOne(opintokokonaisuudet.get(0).getId());
            assertThat(sisaltoViite.getOpintokokonaisuus().getKoodi()).isNotNull();
        });

        SisaltoViiteDto.Matala sisaltoviiteDto = sisaltoViiteService.getSisaltoViite(vstOps.getKoulutustoimija().getId(), vstOps.getId(), opintokokonaisuudet.get(0).getId());
        sisaltoviiteDto.getOpintokokonaisuus().setKoodi(KoodistoUriArvo.OPINTOKOKONAISUUDET + "_" + 5555);
        sisaltoViiteService.updateSisaltoViite(vstOps.getKoulutustoimija().getId(), vstOps.getId(), opintokokonaisuudet.get(0).getId(), sisaltoviiteDto);
        sisaltoviiteDto = sisaltoViiteService.getSisaltoViite(vstOps.getKoulutustoimija().getId(), vstOps.getId(), opintokokonaisuudet.get(0).getId());
        assertThat(sisaltoviiteDto.getOpintokokonaisuus().getKoodi()).isNotEqualTo(KoodistoUriArvo.OPINTOKOKONAISUUDET + "_" + 5555);

    }

    @Test
    public void test_EP_2941() {
        OpintokokonaisuusTavoite tavoite = new OpintokokonaisuusTavoite();
        assertThatCode(() -> OpintokokonaisuusTavoite.copy(tavoite)).doesNotThrowAnyException();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @Rollback
    public void test_ops_pohjasta_tekstien_sailyvyys() {
        useProfileVst();

        OpetussuunnitelmaBaseDto pohjaOps = createOpetussuunnitelma(ops -> {
            ops.setTyyppi(OpsTyyppi.OPSPOHJA);
            ops.setKoulutustyyppi(KoulutusTyyppi.VAPAASIVISTYSTYO);
        });
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), pohjaOps.getId());
        sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), pohjaOps.getId(), root.getId(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
            sisaltoViiteDto.setTekstiKappale(
                    new TekstiKappaleDto(LokalisoituTekstiDto.of("pohjanotsikko"), LokalisoituTekstiDto.of("pohjanteksti"), Tila.LUONNOS));
        }));

        List<SisaltoViiteDto> pohjaviitteet1 = sisaltoViiteService.getSisaltoViitteet(pohjaOps.getKoulutustoimija().getId(), pohjaOps.getId(), SisaltoViiteDto.class);

        OpetussuunnitelmaBaseDto ops1 = createOpetussuunnitelma(ops -> {
            ops.setKoulutustyyppi(KoulutusTyyppi.VAPAASIVISTYSTYO);
            ops.setPerusteDiaarinumero(null);
            ops.setPerusteId(null);
            ops.setOpsId(pohjaOps.getId());
        });

        List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(ops1.getKoulutustoimija().getId(), ops1.getId(), SisaltoViiteDto.class);
        List<SisaltoViiteDto> tekstikappaleet1 = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());

        assertThat(tekstikappaleet1).hasSize(1);
        assertThat(tekstikappaleet1.get(0).getPohjanTekstikappale()).isNotNull();
        assertThat(tekstikappaleet1.get(0).getPohjanTekstikappale().getTeksti().get(Kieli.FI)).isEqualTo("pohjanteksti");
        assertThat(tekstikappaleet1.get(0).getTekstiKappale().getTeksti()).isNull();

        tekstikappaleet1.get(0).getTekstiKappale().setTeksti(LokalisoituTekstiDto.of("opsinteksti"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), ops1.getId(), tekstikappaleet1.get(0).getId(), tekstikappaleet1.get(0));

        OpetussuunnitelmaBaseDto ops2 = createOpetussuunnitelma(ops -> {
            ops.setKoulutustyyppi(KoulutusTyyppi.VAPAASIVISTYSTYO);
            ops.setPerusteDiaarinumero(null);
            ops.setPerusteId(null);
            ops.setOpsId(ops1.getId());
        });

        sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(ops2.getKoulutustoimija().getId(), ops2.getId(), SisaltoViiteDto.class);
        List<SisaltoViiteDto> tekstikappaleet2 = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());

        assertThat(tekstikappaleet2).hasSize(1);
        assertThat(tekstikappaleet2.get(0).getPohjanTekstikappale()).isNotNull();
        assertThat(tekstikappaleet2.get(0).getPohjanTekstikappale().getTeksti().get(Kieli.FI)).isEqualTo("pohjanteksti");
        assertThat(tekstikappaleet2.get(0).getTekstiKappale().getTeksti()).isNotNull();
        assertThat(tekstikappaleet2.get(0).getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("opsinteksti");

        tekstikappaleet2.get(0).getTekstiKappale().setTeksti(LokalisoituTekstiDto.of("opsinteksti2"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), ops2.getId(), tekstikappaleet2.get(0).getId(), tekstikappaleet2.get(0));

        sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(ops1.getKoulutustoimija().getId(), ops1.getId(), SisaltoViiteDto.class);
        tekstikappaleet1 = sisaltoviitteet.stream().filter(viite -> SisaltoTyyppi.TEKSTIKAPPALE.equals(viite.getTyyppi()) && viite.getTekstiKappale() != null).collect(Collectors.toList());

        assertThat(tekstikappaleet1.get(0).getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("opsinteksti");

    }
}

package fi.vm.sade.eperusteet.amosaa.service.vst;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTavoite;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DirtiesContext
@Transactional
@TestPropertySource(properties = {"test.perusteJsonFile: /perusteet/vstPeruste.json"})
public class VapaasivistystyoOpsIT extends AbstractIntegrationTest {

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

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

    }

    @Test
    public void test_EP_2941() {
        OpintokokonaisuusTavoite tavoite = new OpintokokonaisuusTavoite();
        assertThatCode(() -> OpintokokonaisuusTavoite.copy(tavoite)).doesNotThrowAnyException();
    }
}

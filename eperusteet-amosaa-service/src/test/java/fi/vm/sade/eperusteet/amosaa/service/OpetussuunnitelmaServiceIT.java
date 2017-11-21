package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class OpetussuunnitelmaServiceIT extends AbstractIntegrationTest {

    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    private OpetussuunnitelmaBaseDto createOpetussuunnitelma() {
        OpetussuunnitelmaDto ops = new OpetussuunnitelmaDto();
        ops.setKoulutustoimija(getKoulutustoimija());
        ops.setPerusteDiaarinumero("9/011/2008");
        ops.setSuoritustapa("naytto");
        ops.setTyyppi(OpsTyyppi.OPS);
        HashMap<String, String> nimi = new HashMap<>();
        nimi.put("fi", "auto");
        ops.setNimi(new LokalisoituTekstiDto(nimi));
        return opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops);
    }

    @Test
    public void opetussuunnitelmanLuonti() {
        OpetussuunnitelmaBaseDto uusiOpsi = createOpetussuunnitelma();
        assertThat(uusiOpsi).isNotNull();
        assertThat(uusiOpsi.getId()).isNotNull();
        assertThat(uusiOpsi)
                .extracting("tila", "tyyppi", "perusteDiaarinumero")
                .containsExactly(Tila.LUONNOS, OpsTyyppi.OPS, "9/011/2008");
    }

    @Test
    public void opetussuunnitelmanSiirto() {
        useProfileTest1();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(0);

        useProfileTest();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        Optional<KoulutustoimijaYstavaDto> toinen = koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream()
                .filter(ystava -> ystava.getId() != ops.getKoulutustoimija().getId())
                .findFirst();
        assertThat(toinen).isNotEmpty();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(1);

        OpetussuunnitelmaDto paivittynyt = opetussuunnitelmaService.updateKoulutustoimija(
                ops.getKoulutustoimija().getId(),
                ops.getId(),
                toinen.get());
        assertThat(paivittynyt.getKoulutustoimija().getId()).isEqualTo(toinen.get().getId());
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(0);

        useProfileTest1();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(toinen.get().getId())).hasSize(1);
    }

    @Test
    public void opetussuunnitelmanSiirtoVirheellisesti() {
        useProfileTest2();
        Long toimija = koulutustoimijaService.getKoulutustoimija("1.2.246.562.10.79499343246");
        KoulutustoimijaDto toimijaDto = koulutustoimijaService.getKoulutustoimija(toimija);
        useProfileTest();
        assertThat(toimija).isNotNull().isGreaterThan(0);

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(1);

        assertThat(catchThrowable(() -> {
                OpetussuunnitelmaDto paivittynyt = opetussuunnitelmaService.updateKoulutustoimija(
                        ops.getKoulutustoimija().getId(),
                        ops.getId(),
                        toimijaDto);
            }))
            .isInstanceOf(BusinessRuleViolationException.class)
            .hasMessage("siirto-mahdollinen-vain-ystavaorganisaatiolle");
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(1);
    }
}

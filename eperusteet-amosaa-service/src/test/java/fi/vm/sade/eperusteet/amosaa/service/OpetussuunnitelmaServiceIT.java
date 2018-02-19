package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class OpetussuunnitelmaServiceIT extends AbstractIntegrationTest {

    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private KayttajaoikeusService kayttajaoikeusService;

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
    @Rollback
    public void opetussuunnitelmanLuonti() {
        OpetussuunnitelmaBaseDto uusiOpsi = createOpetussuunnitelma();
        assertThat(uusiOpsi).isNotNull();
        assertThat(uusiOpsi.getId()).isNotNull();
        assertThat(uusiOpsi)
                .extracting("tila", "tyyppi", "perusteDiaarinumero")
                .containsExactly(Tila.LUONNOS, OpsTyyppi.OPS, "9/011/2008");
    }

    @Test
    @Rollback
    public void opetussuunnitelmanSiirto() {
        useProfileKP3();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(0);

        useProfileTest();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        Optional<KoulutustoimijaYstavaDto> toinen = koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream()
                .filter(ystava -> !Objects.equals(ystava.getId(), ops.getKoulutustoimija().getId()))
                .findFirst();
        assertThat(toinen).isNotEmpty();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(1);

        OpetussuunnitelmaDto paivittynyt = opetussuunnitelmaService.updateKoulutustoimija(
                ops.getKoulutustoimija().getId(),
                ops.getId(),
                toinen.get());
        assertThat(paivittynyt.getKoulutustoimija().getId()).isEqualTo(toinen.get().getId());
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId())).hasSize(0);

        useProfileKP3();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmat(toinen.get().getId())).hasSize(1);
    }

    @Test
    @Rollback
    public void opetussuunnitelmanSiirtoVirheellisesti() {
        useProfileTmpr();
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

    @Test
    @Rollback
    public void testLuonnoksetEiJulkisia() {
        useProfileKP2();
        OpetussuunnitelmaQueryDto pquery = new OpetussuunnitelmaQueryDto();
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(0);

    }

    @Test
    @Rollback
    public void testOletuksenaEiMuillaOrganisaatioillaOikeuksia() {
        regAllProfiles();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        List<KoulutustoimijaBaseDto> ystavat = koulutustoimijaService.getYhteistyoKoulutustoimijat(getKoulutustoimijaId());
        List<KayttajaoikeusDto> oikeudet = opetussuunnitelmaService.getOikeudet(ops.getKoulutustoimija().getId(), ops.getId());
        assertThat(ystavat).hasSize(0);
        assertThat(oikeudet).hasSize(0);
    }

    private void makeFriendsWithTmpr() {
        useProfileKP2();
        KoulutustoimijaDto kp2 = koulutustoimijaService.getKoulutustoimija(getKoulutustoimijaId());
        kp2.setSalliystavat(true);
        kp2 = koulutustoimijaService.updateKoulutustoimija(kp2.getId(), kp2);

        useProfileTmpr();
        KoulutustoimijaDto tmpr = koulutustoimijaService.getKoulutustoimija(getKoulutustoimijaId());

        tmpr.setSalliystavat(true);
        tmpr = koulutustoimijaService.updateKoulutustoimija(tmpr.getId(), tmpr);
        tmpr.getYstavat().add(Reference.of(kp2.getId()));
        tmpr = koulutustoimijaService.updateKoulutustoimija(tmpr.getId(), tmpr);

        useProfileKP2();
        kp2.getYstavat().add(Reference.of(tmpr.getId()));
        kp2 = koulutustoimijaService.updateKoulutustoimija(kp2.getId(), kp2);

        assertThat(kp2.getYstavat()).contains(Reference.of(tmpr.getId()));
        assertThat(tmpr.getYstavat()).contains(Reference.of(kp2.getId()));
    }

    @Test
    @Rollback
    public void testKoulutustoimijaKayttajaoikeudet() {
        List<KayttajaDto> kaikki = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId());
        assertThat(kaikki)
                .extracting(KayttajaDto::getOid)
                .containsExactlyInAnyOrder("kp2");
    }

    @Test
    @Rollback
    public void testKoulutustoimijaKayttajaoikeudetKaikkiTestiorganisaatiot() {
        regAllProfiles();
        useProfileKP2();
        List<KayttajaDto> kaikki = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId());
        assertThat(kaikki)
                .extracting(KayttajaDto::getOid)
                .containsExactlyInAnyOrder("kp1", "kp2", "kp2user2", "kp3");

    }

    @Test
    @Rollback
    public void testKoulutustoimijaKayttajaoikeudetYstavaorganisaatiolla() {
        regAllProfiles();
        makeFriendsWithTmpr();
        useProfileKP2();
        List<KayttajaDto> kaikki = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId());
        assertThat(kaikki)
                .extracting(KayttajaDto::getOid)
                .containsExactlyInAnyOrder("kp1", "kp2", "kp2user2", "kp3", "tmpr");
    }

    @Test
    @Rollback
    public void testKoulutustoimijaYstavatOpsHenkiloOikeudet() {
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        makeFriendsWithTmpr();
        List<KoulutustoimijaYstavaDto> ystavatoimijat = koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId());
        assertThat(ystavatoimijat)
                .extracting(KoulutustoimijaBaseDto::getOrganisaatio)
                .containsExactlyInAnyOrder(oidTmpr);
        List<KayttajaoikeusDto> oikeudet = opetussuunnitelmaService.getOikeudet(getKoulutustoimijaId(), ops.getId());
        assertThat(oikeudet).hasSize(0);
        KayttajaoikeusDto tmprOikeus = updateUserOikeus(getKoulutustoimijaId(), ops.getId(), KayttajaoikeusTyyppi.LUKU, "tmpr");
        oikeudet = opetussuunnitelmaService.getOikeudet(getKoulutustoimijaId(), ops.getId());
        assertThat(oikeudet).hasSize(1);
        assertThat(oikeudet.get(0))
                .hasFieldOrPropertyWithValue("id", tmprOikeus.getId())
                .hasFieldOrPropertyWithValue("oikeus", KayttajaoikeusTyyppi.LUKU);
        tmprOikeus = updateUserOikeus(getKoulutustoimijaId(), ops.getId(), KayttajaoikeusTyyppi.ESTETTY, "tmpr");
        oikeudet = opetussuunnitelmaService.getOikeudet(getKoulutustoimijaId(), ops.getId());
        assertThat(oikeudet).hasSize(0);
    }

    @Test
    @Rollback
    public void testYstavaorganisaationJasenVoiLadataOpetussuunnitelmat() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();

        // Annetaan vierasorganisaation henkilölle oikeus
        makeFriendsWithTmpr();
        updateUserOikeus(getKoulutustoimijaId(), ops.getId(), KayttajaoikeusTyyppi.LUKU, "tmpr");
        useProfileTmpr();
        List<OpetussuunnitelmaDto> opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId());
        assertThat(opsit)
                .extracting(OpetussuunnitelmaBaseDto::getId)
                .containsExactlyInAnyOrder(ops.getId());
        OpetussuunnitelmaDto ystavaops = opsit.get(0);
        ystavaops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), ystavaops.getId());
        assertThat(ystavaops).hasFieldOrPropertyWithValue("id", ops.getId());

        // Estetään organisaatiolta ystävyys
        useProfileKP2();
        KoulutustoimijaDto toimija = koulutustoimijaService.getKoulutustoimija(getKoulutustoimijaId());
        Reference vanhaYstava = toimija.getYstavat().iterator().next();
        toimija.getYstavat().clear();
        toimija = koulutustoimijaService.updateKoulutustoimija(getKoulutustoimijaId(), toimija);

        useProfileTmpr();
        opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId());
        assertThat(opsit).isEmpty();

        useProfileKP2();
        toimija.getYstavat().add(vanhaYstava);
        toimija = koulutustoimijaService.updateKoulutustoimija(getKoulutustoimijaId(), toimija);

        useProfileTmpr();
        opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId());
        assertThat(opsit).hasSize(1);

        // Estetään käyttäjältä oikeus
        useProfileKP2();
        koulutustoimijaService.updateKoulutustoimija(getKoulutustoimijaId(), toimija);
        updateUserOikeus(getKoulutustoimijaId(), ops.getId(), KayttajaoikeusTyyppi.ESTETTY, "tmpr");

        useProfileTmpr();
        opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId());
        assertThat(opsit).isEmpty();

    }


}

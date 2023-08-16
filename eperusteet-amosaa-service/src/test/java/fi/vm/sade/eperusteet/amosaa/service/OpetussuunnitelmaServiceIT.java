package fi.vm.sade.eperusteet.amosaa.service;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.JotpaTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuInternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaKtoDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class OpetussuunnitelmaServiceIT extends AbstractIntegrationTest {

    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private KayttajaoikeusService kayttajaoikeusService;

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

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
    public void opetussuunnitelmanVirheellinenSiirto() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto kp2ops = createOpetussuunnitelma();

        // Siirto ilman oikeutta organisaatioon
        useProfileKP1();
        assertThat(catchThrowable(() -> {
                opetussuunnitelmaService.updateKoulutustoimija(getKoulutustoimijaId(), kp2ops.getId(), null);
            }))
            .isInstanceOf(AccessDeniedException.class);

        // Siirron yritys ystäväoikeuksilla
        useProfileKP2();
        updateUserOikeus(getKoulutustoimijaId(), kp2ops.getId(), KayttajaoikeusTyyppi.HALLINTA, "kp1");

        useProfileKP1();
        assertThat(catchThrowable(() -> {
                opetussuunnitelmaService.updateKoulutustoimija(getKoulutustoimijaId(), kp2ops.getId(), null);
            }))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @Rollback
    public void testOpetussuunnitelmanRevisiot() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto opsLuonti = createOpetussuunnitelma();
        assertThat(opetussuunnitelmaService.getRevisions(getKoulutustoimijaId(), opsLuonti.getId())).isEmpty();

        // FIXME
//        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), opsLuonti.getId());
//        ops.setKommentti("kommentti");
//        ops.setHyvaksyja("foo");
//        opetussuunnitelmaService.update(getKoulutustoimijaId(), ops.getId(), ops);
//        assertThat(opetussuunnitelmaService.getRevisions(getKoulutustoimijaId(), ops.getId())).isNotEmpty();
//        Revision latestRevision = opetussuunnitelmaService.getLatestRevision(getKoulutustoimijaId(), ops.getId());
//        assertThat(latestRevision).isNotNull();
//        assertThat(opetussuunnitelmaService.getLatestRevisionId(getKoulutustoimijaId(), ops.getId())).isGreaterThanOrEqualTo(0);
//        assertThat(opetussuunnitelmaService.getData(getKoulutustoimijaId(), ops.getId(), latestRevision.hashCode())).isNotNull();
    }

    @Test
    @Rollback
    public void opetussuunnitelmanSiirto() {
        PageRequest pageRequest = new PageRequest(0, 25);

        useProfileKP3();
        assertThat(opetussuunnitelmaService
                .getOpetussuunnitelmat(getKoulutustoimijaId(), pageRequest, new OpsHakuDto())).hasSize(0);

        useProfileTest();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        Optional<KoulutustoimijaYstavaDto> toinen = koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream()
                .filter(ystava -> !Objects.equals(ystava.getId(), ops.getKoulutustoimija().getId()))
                .findFirst();
        assertThat(toinen).isNotEmpty();
        assertThat(opetussuunnitelmaService
                .getOpetussuunnitelmat(getKoulutustoimijaId(), pageRequest, new OpsHakuDto())).hasSize(1);

        OpetussuunnitelmaDto paivittynyt = opetussuunnitelmaService.updateKoulutustoimija(
                ops.getKoulutustoimija().getId(),
                ops.getId(),
                toinen.get());
        assertThat(paivittynyt.getKoulutustoimija().getId()).isEqualTo(toinen.get().getId());
        assertThat(opetussuunnitelmaService
                .getOpetussuunnitelmat(getKoulutustoimijaId(), pageRequest, new OpsHakuDto())).hasSize(0);

        useProfileKP3();
        assertThat(opetussuunnitelmaService
                .getOpetussuunnitelmat(toinen.get().getId(), pageRequest, new OpsHakuDto())).hasSize(1);
    }

    @Test
    @Rollback
    public void opetussuunnitelmanSiirtoVirheellisesti() {
        PageRequest pageRequest = new PageRequest(0, 25);

        useProfileTmpr();
        Long toimija = koulutustoimijaService.getKoulutustoimija("1.2.246.562.10.79499343246");
        KoulutustoimijaDto toimijaDto = koulutustoimijaService.getKoulutustoimija(toimija);
        useProfileTest();
        assertThat(toimija).isNotNull().isGreaterThan(0);

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        assertThat(opetussuunnitelmaService
                .getOpetussuunnitelmat(getKoulutustoimijaId(), pageRequest, new OpsHakuDto())).hasSize(1);

        assertThat(catchThrowable(() -> {
                OpetussuunnitelmaDto paivittynyt = opetussuunnitelmaService.updateKoulutustoimija(
                        ops.getKoulutustoimija().getId(),
                        ops.getId(),
                        toimijaDto);
            }))
            .isInstanceOf(BusinessRuleViolationException.class)
            .hasMessage("siirto-mahdollinen-vain-ystavaorganisaatiolle");
        assertThat(opetussuunnitelmaService
                .getOpetussuunnitelmat(getKoulutustoimijaId(), pageRequest, new OpsHakuDto())).hasSize(1);
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
    public void testFindopetussuunnitelmatByPerusteId() {
        useProfileKP2();
        createOpsWithCachedPeruste("111/111", 1l);
        createOpsWithCachedPeruste("111/222", 1l);
        createOpsWithCachedPeruste("111/111", 11l);
        createOpsWithCachedPeruste("222/222", 2l);
        createOpsWithCachedPeruste("333/333", 3l);

        OpetussuunnitelmaQueryDto pquery = new OpetussuunnitelmaQueryDto();
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));

        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, new OpetussuunnitelmaQueryDto()).getTotalElements()).isEqualTo(5);

        pquery.setPerusteId(11l);
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(1);

        pquery.setPerusteId(1l);
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(2);

        pquery.setPerusteId(null);
        pquery.setPerusteenDiaarinumero("111/111");
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(2);

        pquery.setPerusteId(1l);
        pquery.setPerusteenDiaarinumero("111/111");
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(3);

        pquery.setPerusteId(2l);
        pquery.setPerusteenDiaarinumero("222/222");
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(1);

        pquery.setPerusteId(3l);
        pquery.setPerusteenDiaarinumero("333/333");
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(1);
    }

    private OpetussuunnitelmaBaseDto createOpsWithCachedPeruste(String diaarinumero, Long perusteId) {

        CachedPeruste peruste = cachedPerusteRepository.findOne(perusteId);

        if (peruste == null) {
            peruste = new CachedPeruste();
            peruste.setDiaarinumero(diaarinumero);
            peruste.setPerusteId(perusteId);
            peruste.setPeruste("{}");
            cachedPerusteRepository.save(peruste);
        }

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma(opss -> {
            opss.setPerusteDiaarinumero(diaarinumero);
        });

        Opetussuunnitelma opsEntity = opetussuunnitelmaRepository.findOne(ops.getId());
        opsEntity.setTila(Tila.JULKAISTU);
        opsEntity.setPeruste(peruste);
        opetussuunnitelmaRepository.save(opsEntity);

        return mapper.map(opsEntity, OpetussuunnitelmaBaseDto.class);
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
        tmpr.setYstavat(new HashSet<>());
        tmpr = koulutustoimijaService.updateKoulutustoimija(tmpr.getId(), tmpr);
        tmpr.getYstavat().add(Reference.of(kp2.getId()));
        tmpr = koulutustoimijaService.updateKoulutustoimija(tmpr.getId(), tmpr);

        useProfileKP2();
        kp2.setYstavat(new HashSet<>());
        kp2.getYstavat().add(Reference.of(tmpr.getId()));
        kp2 = koulutustoimijaService.updateKoulutustoimija(kp2.getId(), kp2);

        assertThat(kp2.getYstavat()).contains(Reference.of(tmpr.getId()));
        assertThat(tmpr.getYstavat()).contains(Reference.of(kp2.getId()));
    }

    @Test
    @Rollback
    public void testKoulutustoimijaKayttajaoikeudet() {
        List<KayttajaKtoDto> kaikki = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId());
        assertThat(kaikki)
                .extracting(KayttajaDto::getOid)
                .containsExactlyInAnyOrder("kp2", "1.22.3.4.5.kp2", "1.2.3.4.5.kp1", "tmpr");
    }

    @Test
    @Rollback
    public void testKoulutustoimijaKayttajaoikeudetKaikkiTestiorganisaatiot() {
        regAllProfiles();
        useProfileKP2();
        List<KayttajaKtoDto> kaikki = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId());
        assertThat(kaikki)
                .extracting(KayttajaDto::getOid)
                .containsExactlyInAnyOrder("kp1", "kp2", "1.22.3.4.5.kp2", "1.2.3.4.5.kp1", "tmpr");

    }

    @Test
    @Rollback
    public void testKoulutustoimijaKayttajaoikeudetKaikkiJaYstavaorganisaatiolla() {
        regAllProfiles();
        makeFriendsWithTmpr();
        useProfileKP2();
        List<KayttajaKtoDto> kaikki = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId());
        assertThat(kaikki)
                .extracting(KayttajaDto::getOid)
                .containsExactlyInAnyOrder("kp1", "kp2", "tmpr", "1.22.3.4.5.kp2", "1.2.3.4.5.kp1", "1.22.3.4.5.TMPR");
    }

    @Test
    @Rollback
    public void testKoulutustoimijaKayttajaoikeudetYstavaorganisaatiolla() {
        regAllProfiles();
        makeFriendsWithTmpr();
        useProfileKP2();
        List<KayttajaKtoDto> ystavat = kayttajanTietoService.getYstavaOrganisaatioKayttajat(getKoulutustoimijaId());
        assertThat(ystavat)
                .extracting(KayttajaDto::getOid)
                .containsExactlyInAnyOrder("kp1", "1.2.3.4.5.kp1", "1.22.3.4.5.TMPR");
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
        List<OpetussuunnitelmaDto> opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId(), null);
        assertThat(opsit)
                .extracting(OpetussuunnitelmaBaseDto::getId)
                .containsExactlyInAnyOrder(ops.getId());
        OpetussuunnitelmaDto ystavaops = opsit.get(0);
        ystavaops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), ystavaops.getId());
        assertThat(ystavaops).hasFieldOrPropertyWithValue("id", ops.getId());

        // Testataan käyttäjän henkilökohtaiset oikeudet
        List<KayttajaoikeusDto> oikeuslista = kayttajaoikeusService.getKayttooikeudet();
        assertThat(oikeuslista).hasSize(1);
        assertThat(oikeuslista.get(0))
                .hasFieldOrPropertyWithValue("opetussuunnitelma", Reference.of(ops.getId()))
                .hasFieldOrPropertyWithValue("oikeus", KayttajaoikeusTyyppi.LUKU);

        // Käyttäjän organisaatio-oikeudet
        ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<Long>>> organisaatiooikeudet = kayttajaoikeusService.getOrganisaatiooikeudet(PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA);
        assertThat(organisaatiooikeudet.getBody()).containsKeys(PermissionEvaluator.RolePermission.ADMIN, PermissionEvaluator.RolePermission.CRUD, PermissionEvaluator.RolePermission.READ_UPDATE, PermissionEvaluator.RolePermission.READ);
        assertThat(organisaatiooikeudet.getBody()).hasSize(4);


        // Estetään organisaatiolta ystävyys
        useProfileKP2();
        KoulutustoimijaDto toimija = koulutustoimijaService.getKoulutustoimija(getKoulutustoimijaId());
        Reference vanhaYstava = toimija.getYstavat().iterator().next();
        toimija.getYstavat().clear();
        toimija = koulutustoimijaService.updateKoulutustoimija(getKoulutustoimijaId(), toimija);

        useProfileTmpr();
        opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId(), Sets.newHashSet(KoulutusTyyppi.PERUSTUTKINTO));
        assertThat(opsit).isEmpty();

        useProfileKP2();
        toimija.getYstavat().add(vanhaYstava);
        toimija = koulutustoimijaService.updateKoulutustoimija(getKoulutustoimijaId(), toimija);

        // Palautetaan
        useProfileTmpr();
        opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId(), null);
        assertThat(opsit).hasSize(1);
        ystavaops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), ops.getId());
        assertThat(ystavaops).hasFieldOrPropertyWithValue("id", ops.getId());

        // Estetään käyttäjältä oikeus
        useProfileKP2();
        koulutustoimijaService.updateKoulutustoimija(getKoulutustoimijaId(), toimija);
        updateUserOikeus(getKoulutustoimijaId(), ops.getId(), KayttajaoikeusTyyppi.ESTETTY, "tmpr");

        useProfileTmpr();
        opsit = opetussuunnitelmaService.getOtherOpetussuunnitelmat(getKoulutustoimijaId(), null);
        assertThat(opsit).isEmpty();

        assertThat(catchThrowable(() -> {
            opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), ops.getId());
        }))
        .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @Rollback
    public void testKoulutustoimijanHakuOperussuunnitelmalla() {
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        assertThat(opetussuunnitelmaService.getKoulutustoimijaId(ops.getId()))
                .hasFieldOrPropertyWithValue("id", getKoulutustoimijaId());

        assertThat(catchThrowable(() -> {
            opetussuunnitelmaService.getKoulutustoimijaId(12341234L);
        }))
        .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @Rollback
    public void testOpetussuunnitelmanPaivitys() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto opsLuonti = createOpetussuunnitelma();
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), opsLuonti.getId());
        ops.setEsikatseltavissa(true);
        ops.setHyvaksyja("minä");
        ops = opetussuunnitelmaService.update(getKoulutustoimijaId(), ops.getId(), ops);
        assertThat(ops)
                .hasFieldOrPropertyWithValue("hyvaksyja", "minä")
                .hasFieldOrPropertyWithValue("esikatseltavissa", true);
    }

    @Test
    @Rollback
    public void testOpetussuunnitelmanVirheellinenPaivitys() {
        useProfileKP2();
        assertThat(catchThrowable(() -> {
            OpetussuunnitelmaBaseDto opsLuonti = createOpetussuunnitelma();
            OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), opsLuonti.getId());
            ops.setId(1234L);
            opetussuunnitelmaService.update(getKoulutustoimijaId(), ops.getId(), ops);
        })).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @Rollback
    public void testJulkisetOpetussuunnitelmat() {
        assertThat(opetussuunnitelmaService.getJulkisetOpetussuunnitelmat(getKoulutustoimijaId())).isEmpty();
        assertThat(opetussuunnitelmaService.getJulkaistutPerusteenOpetussuunnitelmat("OPH-12345-1234", OpetussuunnitelmaBaseDto.class)).isEmpty();
        assertThat(opetussuunnitelmaService.getPerusteenOpetussuunnitelmat("OPH-12345-1234", OpetussuunnitelmaBaseDto.class)).isEmpty();
    }

    @Test
    @Rollback
    public void testCachedPerusteet() {
        OpetussuunnitelmaBaseDto opsLuonti = createOpetussuunnitelma();
        assertThat(opetussuunnitelmaService.getOpetussuunnitelmanPeruste(getKoulutustoimijaId(), opsLuonti.getId()))
                .isNotNull();
        OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), opsLuonti.getId());
        assertThat(ops.getTyyppi()).isEqualByComparingTo(OpsTyyppi.OPS);
        assertThat(ops.getPohja()).isNull();
        assertThat(ops).hasFieldOrPropertyWithValue("perusteDiaarinumero", "9/011/2008");
        assertThat(ops.getKoulutustoimija()).isNotNull();
    }

    @Test
    @Rollback
    public void testYleisenPohjanLuominen() {
        // FIXME: Lisää feikkiperuste
//        OpetussuunnitelmaBaseDto yhteinen = createOpetussuunnitelma(ops -> {
//            ops.setTyyppi(OpsTyyppi.YLEINEN);
//        });
//        assertThat(yhteinen).isNotNull();
    }

    @Test
    @Rollback
    public void testPohjatJaYhteisetOsat() {
        useProfileKP2();
        assertThat(catchThrowable(this::createPohja))
            .isInstanceOf(BusinessRuleViolationException.class)
            .hasMessage("ainoastaan-oph-voi-tehda-pohjia");

        useProfileOPH();
        OpetussuunnitelmaBaseDto pohja = createPohja();
        opetussuunnitelmaService.updateTila(getKoulutustoimijaId(), pohja.getId(), Tila.VALMIS, false);
        assertThat(opetussuunnitelmaService.getPohjat()).isEmpty();
        opetussuunnitelmaService.updateTila(getKoulutustoimijaId(), pohja.getId(), Tila.JULKAISTU, false);
        assertThat(opetussuunnitelmaService.getPohjat())
                .extracting(OpetussuunnitelmaBaseDto::getId)
                .contains(pohja.getId());

        useProfileKP2();
        OpetussuunnitelmaBaseDto yhteinen = createOpetussuunnitelma(ops -> {
            ops.setOpsId(pohja.getId());
            ops.setTyyppi(OpsTyyppi.YHTEINEN);
        });

        assertThat(yhteinen.getPohja().getIdLong()).isEqualTo(pohja.getId());
    }

    @Test
    @Rollback
    public void testUpdateKoulutustoimijaPassivoidusta_historialiitos_passiivinen() {

        useProfileKP1();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();

        useProfileKP2();

        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(ops.getId());
        assertThat(opetussuunnitelma.getKoulutustoimija().getId()).isNotEqualTo(getKoulutustoimijaId());

        opetussuunnitelmaService.updateKoulutustoimijaPassivoidusta(getKoulutustoimijaId(), ops.getId());

        opetussuunnitelma = opetussuunnitelmaRepository.findOne(ops.getId());
        assertThat(opetussuunnitelma.getKoulutustoimija().getId()).isEqualTo(getKoulutustoimijaId());
    }

    @Test
    @Rollback
    public void testUpdateKoulutustoimijaPassivoidusta_historialiitos_aktiivinen() {

        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();

        useProfileKP1();

        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(ops.getId());
        assertThat(opetussuunnitelma.getKoulutustoimija().getId()).isNotEqualTo(getKoulutustoimijaId());

        Assertions.assertThatThrownBy(() -> opetussuunnitelmaService.updateKoulutustoimijaPassivoidusta(getKoulutustoimijaId(), ops.getId()))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("siirto-mahdollinen-aiemmin-passivoidulta-organisaatiolta");

        opetussuunnitelma = opetussuunnitelmaRepository.findOne(ops.getId());
        assertThat(opetussuunnitelma.getKoulutustoimija().getId()).isNotEqualTo(getKoulutustoimijaId());
    }

    @Test
    @Rollback
    public void testOpetussuunnitelmaluontiTutkinnonosaInclude() {
        useProfileKP2();

        {
            OpetussuunnitelmaBaseDto opetussuunnitelma = createOpetussuunnitelma();
            List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(getKoulutustoimijaId(), opetussuunnitelma.getId(), SisaltoViiteDto.class);
            assertThat(sisaltoviitteet.stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA))).hasSize(10);
        }

        {
            OpetussuunnitelmaBaseDto opetussuunnitelma = createOpetussuunnitelma(opsLuonti -> {
                opsLuonti.setTutkinnonOsaKoodiIncludes(Sets.newHashSet("tutkinnonosat_104080", "tutkinnonosat_104074"));
            });

            List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(getKoulutustoimijaId(), opetussuunnitelma.getId(), SisaltoViiteDto.class);
            assertThat(sisaltoviitteet.stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA))).hasSize(2);
            assertThat(sisaltoviitteet.stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)))
                    .extracting("tosa.koodi").containsExactlyInAnyOrder("tutkinnonosat_104080", "tutkinnonosat_104074");
        }

        {
            OpetussuunnitelmaBaseDto pohja = createOpetussuunnitelma(opsLuonti -> {
                opsLuonti.setTutkinnonOsaKoodiIncludes(Sets.newHashSet("tutkinnonosat_104080", "tutkinnonosat_104074"));
            });

            OpetussuunnitelmaBaseDto opetussuunnitelma = createOpetussuunnitelma(opsLuonti -> {
                opsLuonti.setOpsId(pohja.getId());
            });

            List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(getKoulutustoimijaId(), opetussuunnitelma.getId(), SisaltoViiteDto.class);
            assertThat(sisaltoviitteet.stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA))).hasSize(2);
            assertThat(sisaltoviitteet.stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)))
                    .extracting("tosa.koodi").containsExactlyInAnyOrder("tutkinnonosat_104080", "tutkinnonosat_104074");
        }

        {
            OpetussuunnitelmaBaseDto pohja = createOpetussuunnitelma(opsLuonti -> {
                opsLuonti.setTutkinnonOsaKoodiIncludes(Sets.newHashSet("tutkinnonosat_104080", "tutkinnonosat_104074"));
            });

            OpetussuunnitelmaBaseDto opetussuunnitelma = createOpetussuunnitelma(opsLuonti -> {
                opsLuonti.setOpsId(pohja.getId());
                opsLuonti.setTutkinnonOsaKoodiIncludes(Sets.newHashSet("tutkinnonosat_104080"));
            });

            List<SisaltoViiteDto> sisaltoviitteet = sisaltoViiteService.getSisaltoViitteet(getKoulutustoimijaId(), opetussuunnitelma.getId(), SisaltoViiteDto.class);
            assertThat(sisaltoviitteet.stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA))).hasSize(1);
            assertThat(sisaltoviitteet.stream().filter(sisaltoviite -> sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA)))
                    .extracting("tosa.koodi").containsExactlyInAnyOrder("tutkinnonosat_104080");
        }
    }

    @Test
    @Rollback
    public void testFindOrganisaatioRyhma() {
        useProfileKP2();
        createOpsWithCachedPeruste("111/111", 1l);
        createOpsWithCachedPeruste("111/222", 1l);

        useProfileKP3();
        setCurrentProfileRyhma();
        createOpsWithCachedPeruste("222/222", 2l);

        OpetussuunnitelmaQueryDto pquery = new OpetussuunnitelmaQueryDto();
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));

        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(2);

        pquery.setOrganisaatioRyhma(true);
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(1);

    }

    @Test
    @Rollback
    public void testFindTutkinnonosanNimella() {
        useProfileKP2();

        createOpetussuunnitelmaJulkaistu();

        createOpetussuunnitelmaJulkaistu(opsLuonti -> {
            opsLuonti.setTutkinnonOsaKoodiIncludes(Sets.newHashSet("tutkinnonosat_104080", "tutkinnonosat_104074"));
        });

        OpetussuunnitelmaQueryDto pquery = new OpetussuunnitelmaQueryDto();
        PageRequest p = new PageRequest(pquery.getSivu(), Math.min(pquery.getSivukoko(), 100));

        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(2);

        pquery.setNimi("yrittäjyys");
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(2);

        pquery.setNimi("cnc-tekniikka");
        assertThat(opetussuunnitelmaService.findOpetussuunnitelmat(p, pquery).getTotalElements()).isEqualTo(1);

    }

    @Test
    @Rollback
    public void testTuvaValidointi() {
        useProfileOPH();

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelmaJulkaistu(opsLuonti -> {
            opsLuonti.setKoulutustyyppi(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
            opsLuonti.setJulkaisukielet(Collections.singleton(Kieli.FI));
        });

        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala sisalto = createSisalto();
        sisalto = sisaltoViiteService.addSisaltoViite(ops.getKoulutustoimija().getId(), ops.getId(), root.getId(), sisalto);
        Validointi validointi = opetussuunnitelmaService.validoi(ops.getKoulutustoimija().getId(), ops.getId());
        assertThat(validointi.getVirheet().stream().filter(virhe -> !virhe.getNavigationNode().getType().equals(NavigationType.tiedot)))
                .extracting(Validointi.Virhe::getKuvaus)
                .contains("kielisisaltoa-ei-loytynyt-opsin-kielilla");

        sisalto.getTekstiKappale().setTeksti(LokalisoituTekstiDto.of(Kieli.FI, "teksti"));
        sisaltoViiteService.updateSisaltoViite(ops.getKoulutustoimija().getId(), ops.getId(), sisalto.getId(), sisalto);
        validointi = opetussuunnitelmaService.validoi(ops.getKoulutustoimija().getId(), ops.getId());
        assertThat(validointi.getVirheet().stream().filter(virhe -> !virhe.getNavigationNode().getType().equals(NavigationType.tiedot)))
                .extracting(Validointi.Virhe::getKuvaus)
                .doesNotContain("kielisisaltoa-ei-loytynyt-opsin-kielilla");
    }

    @Test
    @Rollback
    public void testYstavaOrganisaationOpsinValidointi() {
        useProfileKP1();
        KoulutustoimijaDto kt1 = koulutustoimijaService.getKoulutustoimija(getKoulutustoimijaId());

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma(opsLuonti -> {
        });

        useProfileKP2();
        Long kayttaja2id = kayttaja.getId();
        KoulutustoimijaDto kt2 = koulutustoimijaService.getKoulutustoimija(getKoulutustoimijaId());

        assertThatThrownBy(() -> opetussuunnitelmaService.validoi(ops.getKoulutustoimija().getId(), ops.getId()))
                .hasMessage("Access is denied");

        useProfileKP1();
        kt1.setSalliystavat(true);
        kt1.setYstavat(Sets.newHashSet(Reference.of(kt2.getId())));
        koulutustoimijaService.updateKoulutustoimija(kt1.getId(), kt1);
        KayttajaoikeusDto oikeus = new KayttajaoikeusDto();
        oikeus.setOikeus(KayttajaoikeusTyyppi.HALLINTA);
        opetussuunnitelmaService.updateOikeus(kt1.getId(), ops.getId(), kayttaja2id, oikeus);

        useProfileKP2();
        kt2.setSalliystavat(true);
        kt2.setYstavat(Sets.newHashSet(Reference.of(kt1.getId())));
        koulutustoimijaService.updateKoulutustoimija(kt2.getId(), kt2);

        Validointi validointi = opetussuunnitelmaService.validoi(ops.getKoulutustoimija().getId(), ops.getId());
        assertThat(validointi.getVirheet()).isNotEmpty();
    }

    @Test
    public void testGetKoulutustoimijatKaikki() {
        useProfileKP1();
        createOpetussuunnitelma();
        useProfileKP2();
        createOpetussuunnitelma(ops -> {
            Map<String, String> nimet = new HashMap<>();
            nimet.put("fi", "kaikki");
            nimet.put("sv", "sv");

            LokalisoituTekstiDto nimi = new LokalisoituTekstiDto(nimet);
            ops.setNimi(nimi);
            ops.setJotpatyyppi(JotpaTyyppi.MUU);
        });

        OpsHakuInternalDto haku = createDefaultOpsHakuInternalDto();

        Page<OpetussuunnitelmaDto> opss = opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId(), haku);
        assertThat(opss.getTotalElements()).isEqualTo(1L);

        haku.setNimi("kaikki");
        haku.setJotpa(true);

        opss = opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId(), haku);
        assertThat(opss.getTotalElements()).isEqualTo(1L);

        useProfileKP1();
        opss = opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId(), createDefaultOpsHakuInternalDto());
        assertThat(opss.getTotalElements()).isEqualTo(1L);

        useProfileOPH();
        opss = opetussuunnitelmaService.getOpetussuunnitelmat(getKoulutustoimijaId(), createDefaultOpsHakuInternalDto());
        assertThat(opss.getTotalElements()).isEqualTo(2L);
    }

    private OpsHakuInternalDto createDefaultOpsHakuInternalDto() {
        return OpsHakuInternalDto.builder()
                .koulutustyyppi(Collections.singletonList(KoulutusTyyppi.ERIKOISAMMATTITUTKINTO.toString()))
                .jotpa(false)
                .tyyppi(OpsTyyppi.OPS)
                .julkaistuTaiValmis(false)
                .build();
    }

}

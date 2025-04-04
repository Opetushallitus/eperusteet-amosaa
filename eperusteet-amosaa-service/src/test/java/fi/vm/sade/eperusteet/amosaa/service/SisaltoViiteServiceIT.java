package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.*;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.LiiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class SisaltoViiteServiceIT extends AbstractIntegrationTest {

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private SisaltoViiteService service;

    @Autowired
    private ValidointiService validointiService;

    @Autowired
    private LiiteService liiteService;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Test
    @Rollback
    public void testVierasOsaPerusteesta() {
        useProfileKP3();

        // Ops
        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setKoulutustoimija(getKoulutustoimija());
        ops.setPerusteDiaarinumero("OPH-12345-2018");
        ops.setSuoritustapa("naytto");
        ops.setTyyppi(OpsTyyppi.OPS);
        HashMap<String, String> opsNimi = new HashMap<>();
        opsNimi.put("fi", "jaettu");
        ops.setNimi(new LokalisoituTekstiDto(opsNimi));

        // Sisältö viite
        SisaltoViiteDto.Matala viiteDto = new SisaltoViiteDto.Matala();
        viiteDto.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);

        TutkinnonosaDto tutkinnonosaDto = new TutkinnonosaDto();
        tutkinnonosaDto.setTyyppi(TutkinnonosaTyyppi.VIERAS);

        VierasTutkinnonosaDto vierasTutkinnonosaDto = new VierasTutkinnonosaDto();
        vierasTutkinnonosaDto.setPerusteId(3489211L);
        vierasTutkinnonosaDto.setTosaId(577941L);

        tutkinnonosaDto.setVierastutkinnonosa(vierasTutkinnonosaDto);

        viiteDto.setTosa(tutkinnonosaDto);

        TekstiKappaleDto tekstiKappaleDto = new TekstiKappaleDto();
        viiteDto.setTekstiKappale(tekstiKappaleDto);

        OpetussuunnitelmaBaseDto uusi = createOpetussuunnitelma();
        Long ktId = getKoulutustoimijaId();
        Long opsId = uusi.getId();

        SisaltoViiteKevytDto tutkinnonOsat = getFirstOfType(ktId, opsId, SisaltoTyyppi.TUTKINNONOSAT);

        SisaltoViiteDto.Matala uusiViite = sisaltoViiteService.addSisaltoViite(ktId, opsId, tutkinnonOsat.getId(), viiteDto);
        assertThat(uusiViite).isNotNull();
    }

    @Test
    @Rollback
    public void testTutkinnonOsanToteutuksenVapaitaTeksteja() {
        useProfileKP3();

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();

        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto(sisaltoViiteDto -> {
        	sisaltoViiteDto.setSuorituspolku(new SuorituspolkuDto());
        	sisaltoViiteDto.getSuorituspolku().getRivit().add(SuorituspolkuRiviDto.of("76044b5b-1f4b-4587-b30d-23b7d3c2608d", true, null));
        	sisaltoViiteDto.getSuorituspolku().getRivit().add(SuorituspolkuRiviDto.of("428e7f22-0a69-43c5-baa5-520296f71169", false, LokalisoituTekstiDto.of("foo")));
        }));

        added.getTekstiKappale().setNimi(LokalisoituTekstiDto.of("uusi nimi"));
        added.getTekstiKappale().setTeksti(LokalisoituTekstiDto.of("uusi teksti"));

        TutkinnonosaDto tutkinnonosaDto = new TutkinnonosaDto();
        added.setTosa(tutkinnonosaDto);

        List<TutkinnonosaToteutusDto> toteutukset = new ArrayList<>();
        TutkinnonosaToteutusDto toteutus = new TutkinnonosaToteutusDto();
        toteutukset.add(toteutus);
        tutkinnonosaDto.setToteutukset(toteutukset);

        List<VapaaTekstiDto> tekstit = new ArrayList<>();
        VapaaTekstiDto teskti = new VapaaTekstiDto();
        teskti.setNimi(LokalisoituTekstiDto.of("Toteutuksen vapaan tesktikappaleen nimi"));
        teskti.setTeksti(LokalisoituTekstiDto.of("Toteutuksen vapaan tesktikappaleen teksti"));
        tekstit.add(teskti);
        toteutus.setVapaat(tekstit);

        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId(), added);

        assertThat(added)
                .extracting(SisaltoViiteDto::getTosa).extracting(TutkinnonosaDto::getToteutukset)
                .isEqualTo(toteutukset);

        assertThat(added.getTosa().getToteutukset().get(0).getVapaat())
                .extracting(vapaa -> vapaa.getNimi().get(Kieli.FI), vapaa -> vapaa.getTeksti().get(Kieli.FI))
                .containsExactly(tuple("Toteutuksen vapaan tesktikappaleen nimi", "Toteutuksen vapaan tesktikappaleen teksti"));

    }

    @Test
    @Rollback
    public void testTekstisisalto() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto(sisaltoViiteDto -> {
        	sisaltoViiteDto.setSuorituspolku(new SuorituspolkuDto());
        	sisaltoViiteDto.getSuorituspolku().getRivit().add(SuorituspolkuRiviDto.of("76044b5b-1f4b-4587-b30d-23b7d3c2608d", true, null));
        	sisaltoViiteDto.getSuorituspolku().getRivit().add(SuorituspolkuRiviDto.of("428e7f22-0a69-43c5-baa5-520296f71169", false, LokalisoituTekstiDto.of("foo")));
        }));
        SisaltoViiteDto.Matala alempi = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId(), createSisalto());
        assertThat(added)
                .extracting(SisaltoViiteDto::getOwner, SisaltoViiteDto::getVanhempi, SisaltoViiteDto::getTyyppi, SisaltoViiteDto::isPakollinen, SisaltoViiteDto::isValmis, SisaltoViiteDto::isLiikkumaton)
                .containsExactly(Reference.of(ops.getId()), Reference.of(root.getId()), SisaltoTyyppi.TEKSTIKAPPALE, false, false, false);
        assertThat(added.getTekstiKappale()).isNotNull();

        added.getTekstiKappale().setNimi(LokalisoituTekstiDto.of("uusi nimi"));
        added.getTekstiKappale().setTeksti(LokalisoituTekstiDto.of("uusi teksti"));
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId(), added);
        SisaltoViiteDto.Matala viite = sisaltoViiteService.getSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId());
        assertThat(viite.getLapset()).containsExactlyInAnyOrder(Reference.of(alempi.getId()));
        assertThat(viite.getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("uusi nimi");
        assertThat(viite.getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("uusi teksti");

        SisaltoViiteDto.Puu puu = sisaltoViiteService.kloonaaTekstiKappale(getKoulutustoimijaId(), ops.getId(), added.getId());
        assertThat(puu.getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("uusi nimi");
        assertThat(puu.getTekstiKappale().getTeksti().get(Kieli.FI)).isEqualTo("uusi teksti");
        assertThat(puu.getLapset()).hasSize(1);

        Long id = added.getId();

        sisaltoViiteService.removeSisaltoViite(getKoulutustoimijaId(), ops.getId(), alempi.getId());
        sisaltoViiteService.removeSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId());

        assertThat(catchThrowable(() -> {
            sisaltoViiteService.getSisaltoViite(getKoulutustoimijaId(), ops.getId(), id);
        })).isInstanceOf(ConstraintViolationException.class);

        assertThat(catchThrowable(() -> {
            sisaltoViiteService.getSisaltoViite(getKoulutustoimijaId(), ops.getId(), alempi.getId());
        })).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @Rollback
    public void testSuorituspolkuU_update() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        List<SisaltoViiteDto> suorituspolut = sisaltoViiteService.getSisaltoviitteet(getKoulutustoimijaId(), ops.getId(), SisaltoTyyppi.SUORITUSPOLUT);
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), suorituspolut.get(0).getId(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.SUORITUSPOLKU);
            sisaltoViiteDto.setSuorituspolku(new SuorituspolkuDto());
            sisaltoViiteDto.getSuorituspolku().getRivit().add(SuorituspolkuRiviDto.of("76044b5b-1f4b-4587-b30d-23b7d3c2608d", true, null));
            sisaltoViiteDto.getSuorituspolku().getRivit().add(SuorituspolkuRiviDto.of("428e7f22-0a69-43c5-baa5-520296f71169", false, LokalisoituTekstiDto.of("foo")));
        }));

        SisaltoViiteDto suorituspolku = service.getSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId());
        assertThat(suorituspolku.getSuorituspolku().getPiilotaPerusteenTeksti()).isNull();
        suorituspolku.getSuorituspolku().setPiilotaPerusteenTeksti(true);
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), ops.getId(), suorituspolku.getId(), suorituspolku);

        suorituspolku = service.getSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId());
        assertThat(suorituspolku.getSuorituspolku().getPiilotaPerusteenTeksti()).isTrue();
    }

    @Test
    @Rollback
    public void testOpsinSisallonUudelleenjarjestaminen() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto());
        SisaltoViiteRakenneDto rakenne = sisaltoViiteService.getRakenne(getKoulutustoimijaId(), ops.getId());
        assertThat(rakenne.getLapset().get(rakenne.getLapset().size() - 1)).hasFieldOrPropertyWithValue("id", added.getId());

        int lastIdx = rakenne.getLapset().size() - 1;
        SisaltoViiteRakenneDto a = rakenne.getLapset().get(0);
        SisaltoViiteRakenneDto b = rakenne.getLapset().get(lastIdx);
        rakenne.getLapset().set(0, b);
        rakenne.getLapset().set(lastIdx, a);
        sisaltoViiteService.reorderSubTree(getKoulutustoimijaId(), ops.getId(), root.getId(), rakenne);
        rakenne = sisaltoViiteService.getRakenne(getKoulutustoimijaId(), ops.getId());
        assertThat(rakenne.getLapset().get(0)).hasFieldOrPropertyWithValue("id", b.getId());
        assertThat(rakenne.getLapset().get(lastIdx)).hasFieldOrPropertyWithValue("id", a.getId());
    }

    @Test
    @Rollback
    public void testOpsinSuorituspolkuRakenteenLuominenIlmanOmaaSisaltoa() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getLapset().get(0).getIdLong(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.SUORITUSPOLKU);
        }));
        List<SuorituspolkuRakenneDto> rakenne = sisaltoViiteService.getSuorituspolkurakenne(getKoulutustoimijaId(), ops.getId());
        assertThat(rakenne).hasSize(1);
        SuorituspolkuRakenneDto polku = rakenne.get(0);
        assertThat(polku)
                .hasFieldOrPropertyWithValue("tunniste", UUID.fromString("16a37d87-cb5f-41a1-94f1-27fb1fa6d191"))
                .hasFieldOrPropertyWithValue("paikallinenKuvaus", null);
        assertThat(polku.getOsat()).hasSize(5);
        assertThat(((SuorituspolkuRakenneDto)(polku.getOsat().get(0))).getOsat()).hasSize(3);
        assertThat(((SuorituspolkuRakenneDto)(polku.getOsat().get(1))).getOsat()).hasSize(2);
        assertThat(((SuorituspolkuRakenneDto)(polku.getOsat().get(2))).getOsat()).hasSize(4);
        assertThat(((SuorituspolkuRakenneDto)(polku.getOsat().get(3))).getOsat()).hasSize(1);
        assertThat(((SuorituspolkuRakenneDto)(polku.getOsat().get(4))).getOsat()).hasSize(2);

        assertThat(polku.getOsat().get(0))
                .hasFieldOrPropertyWithValue("tunniste", UUID.fromString("d35fb695-f181-4e49-b4b9-c64a85819d0a"))
                .hasFieldOrPropertyWithValue("paikallinenKuvaus", null);
    }

    @Test
    @Rollback
    @Ignore
    public void testSisaltoHierarkianKopiointi() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
        }));
    }

    @Test
    @Rollback
    public void testOpsinSuorituspolkuRakenteenLuominenOmallaSisallolla() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getLapset().get(0).getIdLong(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.SUORITUSPOLKU);
            sisaltoViiteDto.setSuorituspolku(new SuorituspolkuDto());
            sisaltoViiteDto.getSuorituspolku().getRivit()
                    .add(SuorituspolkuRiviDto.of("76044b5b-1f4b-4587-b30d-23b7d3c2608d", true, null));
            sisaltoViiteDto.getSuorituspolku().getRivit()
                    .add(SuorituspolkuRiviDto.of("428e7f22-0a69-43c5-baa5-520296f71169", false, LokalisoituTekstiDto.of("foo")));
        }));

        assertThat(added.getSuorituspolku().getRivit())
                .extracting("rakennemoduuli")
                .containsExactlyInAnyOrder(
                        UUID.fromString("76044b5b-1f4b-4587-b30d-23b7d3c2608d"),
                        UUID.fromString("428e7f22-0a69-43c5-baa5-520296f71169"));
        List<SuorituspolkuRakenneDto> rakenne = sisaltoViiteService.getSuorituspolkurakenne(getKoulutustoimijaId(), ops.getId());
        assertThat(rakenne).isNotNull();

        assertThat(rakenne).hasSize(1);
        SuorituspolkuRakenneDto polku = rakenne.get(0);
        assertThat(polku)
                .hasFieldOrPropertyWithValue("tunniste", UUID.fromString("16a37d87-cb5f-41a1-94f1-27fb1fa6d191"))
                .hasFieldOrPropertyWithValue("paikallinenKuvaus", null);
        assertThat(polku.getOsat())
                .extracting("tunniste")
                .containsExactly(
                        UUID.fromString("d35fb695-f181-4e49-b4b9-c64a85819d0a"),
                        UUID.fromString("428e7f22-0a69-43c5-baa5-520296f71169"),
                        UUID.fromString("ab3ea166-2ea6-4a6e-82f6-ab8d26abb92a"),
                        UUID.fromString("49c79989-bd21-45ff-a5ee-685599300001"));

        SuorituspolkuRakenneDto osa = (SuorituspolkuRakenneDto)polku.getOsat().get(1);
        assertThat(osa.getPaikallinenKuvaus().getKuvaus().get(Kieli.FI))
                .isEqualTo("foo");
    }
    @Test
    @Rollback
    public void testPaikallisenTutkinnonOsanKoodi() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());

        SisaltoViiteKevytDto tutkinnonosat = getFirstOfType(getKoulutustoimijaId(), ops.getId(), SisaltoTyyppi.TUTKINNONOSAT);

        // Lisää toteutussuunnitelmaan paikallisen osan annetulla koodilla
        Function<String, SisaltoViiteDto.Matala> addOsaWithKoodi = (String koodi) ->
                sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), tutkinnonosat.getId(), createSisalto(osa -> {
                    osa.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
                    TutkinnonosaDto tosa = new TutkinnonosaDto();
                    tosa.setTyyppi(TutkinnonosaTyyppi.OMA);
                    OmaTutkinnonosaDto oma = new OmaTutkinnonosaDto();
                    oma.setKoodi(koodi);
                    tosa.setOmatutkinnonosa(oma);
                    osa.setTosa(tosa);
                }));

        assertThatCode(() -> addOsaWithKoodi.apply("999")).doesNotThrowAnyException();

        { // Validi
            SisaltoViiteDto.Matala osa = addOsaWithKoodi.apply("999999999");
            assertThat(validointiService.tutkinnonOsanValidointivirheet(ops.getId(), osa.getId()))
                    .doesNotContain("oma-tutkinnon-osa-virheellinen-koodi");
        }

        { // Epävalidi
            SisaltoViiteDto.Matala osa = addOsaWithKoodi.apply("999");
            assertThat(validointiService.tutkinnonOsanValidointivirheet(ops.getId(), osa.getId()))
                    .contains("oma-tutkinnon-osa-virheellinen-koodi");
        }

        { // Epävalidi
            SisaltoViiteDto.Matala osa = addOsaWithKoodi.apply("9999999999");
            assertThat(validointiService.tutkinnonOsanValidointivirheet(ops.getId(), osa.getId()))
                    .contains("oma-tutkinnon-osa-virheellinen-koodi");
        }
    }

    @Test
    @Rollback
    public void testUpdateOpetussuunnitelmaPiilotetutSisaltoviitteet_ei_piilotuksia() {

        useProfileKP2();
        Opetussuunnitelma ops = createOpetussuunnitelmaJulkaistu();

        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(),
                root.getId(), createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setSuorituspolku(new SuorituspolkuDto());
                }));

        sisaltoViiteService.updateOpetussuunnitelmaPiilotetutSisaltoviitteet(added, ops);

        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(ops.getId());

        assertThat(opetussuunnitelma.getOsaamisalat()).hasSize(2);
        assertThat(opetussuunnitelma.getOsaamisalat()).containsExactlyInAnyOrder("osaamisala-1", "osaamisala-2");

        assertThat(opetussuunnitelma.getTutkintonimikkeet()).hasSize(2);
        assertThat(opetussuunnitelma.getTutkintonimikkeet()).containsExactlyInAnyOrder("tutkintonimike-1",
                "tutkintonimike-2");

    }

    @Test
    @Rollback
    public void testUpdateOpetussuunnitelmaPiilotetutSisaltoviitteet_piilotuksia() {

        useProfileKP2();
        Opetussuunnitelma ops = createOpetussuunnitelmaJulkaistu();

        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(),
                root.getId(), createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setSuorituspolku(new SuorituspolkuDto());
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("49c79989-bd21-45ff-a5ee-685599300002", false, null));
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("49c79989-bd21-45ff-a5ee-685599300003", true, null));
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("49c79989-bd21-45ff-a5ee-685599300004", true, null));
                    sisaltoViiteDto.getSuorituspolku().getRivit()
                            .add(SuorituspolkuRiviDto.of("49c79989-bd21-45ff-a5ee-685599300005", false, null));
                }));

        sisaltoViiteService.updateOpetussuunnitelmaPiilotetutSisaltoviitteet(added, ops);

        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(ops.getId());

        assertThat(opetussuunnitelma.getOsaamisalat()).hasSize(1);
        assertThat(opetussuunnitelma.getOsaamisalat()).containsExactly("osaamisala-1");

        assertThat(opetussuunnitelma.getTutkintonimikkeet()).hasSize(0);

    }

    @Test
    @Rollback
    public void testCopySisaltoViiteet() {

        useProfileKP2();
        Opetussuunnitelma ops = createOpetussuunnitelmaJulkaistu();
        liiteService.add(getKoulutustoimijaId(), ops.getId(), "txt", "teksti.txt", 1l, new ByteArrayInputStream("tekstia".getBytes()));

        SisaltoViite opsRoot = sisaltoviiteRepository.findOneRoot(ops);
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());

        List<SisaltoViiteDto.Matala> added = Arrays.asList(
                sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
                })), sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getLapset().get(1).getIdLong(), createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
                    sisaltoViiteDto.setTosa(TutkinnonosaDto.builder()
                            .omatutkinnonosa(OmaTutkinnonosaDto.builder()
                                    .ammattitaitovaatimuksetLista(Arrays.asList(AmmattitaitovaatimuksenKohdealueDto.builder()
                                            .vaatimuksenKohteet(Arrays.asList(AmmattitaitovaatimuksenKohdeDto.builder()
                                                    .vaatimukset(Arrays.asList(AmmattitaitovaatimusDto.builder()
                                                            .selite(LokalisoituTekstiDto.of("vaatimusteksti"))
                                                            .build()))
                                                    .build()))
                                            .build()))
                                    .build())
                            .build()
                    );
                })), sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getLapset().get(0).getIdLong(), createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.SUORITUSPOLKU);
                })));

        Opetussuunnitelma ops2 = createOpetussuunnitelmaJulkaistu();

        SisaltoViite ops2Root = sisaltoviiteRepository.findOneRoot(ops2);
        assertThat(ops2Root.getLapset()).hasSize(2);
        assertThat(ops2Root.getLapset().get(1).getTyyppi()).isEqualTo(SisaltoTyyppi.TUTKINNONOSAT);
        assertThat(ops2Root.getLapset().get(1).getLapset()).hasSize(10);
        assertThat(ops2Root.getLapset().get(0).getTyyppi()).isEqualTo(SisaltoTyyppi.SUORITUSPOLUT);
        assertThat(ops2Root.getLapset().get(0).getLapset()).hasSize(0);
        assertThat(liiteService.getAll(getKoulutustoimijaId(), ops.getId())).hasSize(1);
        assertThat(liiteService.getAll(getKoulutustoimijaId(), ops2.getId())).hasSize(0);

        sisaltoViiteService.copySisaltoViiteet(getKoulutustoimijaId(), ops2.getId(), added.stream().map(SisaltoViiteDto.Matala::getId).collect(Collectors.toList()));

        ops2Root = sisaltoviiteRepository.findOneRoot(ops2);
        assertThat(ops2Root.getLapset()).hasSize(3);
        assertThat(ops2Root.getLapset().get(1).getTyyppi()).isEqualTo(SisaltoTyyppi.TUTKINNONOSAT);
        assertThat(ops2Root.getLapset().get(1).getLapset().get(10)
                .getTosa().getOmatutkinnonosa().getAmmattitaitovaatimuksetLista()
                .get(0).getVaatimuksenKohteet().get(0).getVaatimukset().get(0)
                .getSelite().getTeksti().get(Kieli.FI)).isEqualTo("vaatimusteksti");
        assertThat(ops2Root.getLapset().get(1).getLapset()).hasSize(11);
        assertThat(ops2Root.getLapset().get(0).getLapset().get(0).getTyyppi()).isEqualTo(SisaltoTyyppi.SUORITUSPOLKU);
        assertThat(ops2Root.getLapset().get(0).getLapset()).hasSize(1);
        assertThat(ops2Root.getLapset().get(2).getTyyppi()).isEqualTo(SisaltoTyyppi.TEKSTIKAPPALE);
        assertThat(liiteService.getAll(getKoulutustoimijaId(), ops.getId())).hasSize(1);
        assertThat(liiteService.getAll(getKoulutustoimijaId(), ops2.getId())).hasSize(1);
    }

    @Test
    @Rollback
    public void testPaikallisetYhteisetLisaykset() {
        useProfileKP2();
        Opetussuunnitelma ops = createOpetussuunnitelmaJulkaistu();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(ops.getKoulutustoimija().getId(), ops.getId());

        SisaltoViiteKevytDto tutkinnonosat = getFirstOfType(getKoulutustoimijaId(), ops.getId(), SisaltoTyyppi.TUTKINNONOSAT);

        SisaltoViiteDto uusi = sisaltoViiteService.addSisaltoViite(
                ops.getKoulutustoimija().getId(),
                ops.getId(),
                tutkinnonosat.getId(), createSisalto(viite -> {
            viite.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
            viite.setTosa(TutkinnonosaDto.builder()
                    .build());
        }));

        Function<String, OmaOsaAlueDto> createOsaalue = (String id) -> {
            OmaOsaAlueDto paikallinen = new OmaOsaAlueDto();
            paikallinen.setNimi(LokalisoituTekstiDto.of("nimi-" + id));
            paikallinen.setTyyppi(OmaOsaAlueTyyppi.PAIKALLINEN);
            paikallinen.setPerusteenOsaAlueKoodi("1234-" + id);
            paikallinen.setToteutukset(Arrays.asList(OmaOsaAlueToteutusDto.builder()
                            .arvioinnista(TekstiosaDto.of("arvioinnista-" + id))
                            .tavatjaymparisto(TekstiosaDto.of("tavatjaymparisto-" + id))
                    .build()));
            {
                PaikallisetAmmattitaitovaatimukset2019Dto ot = new PaikallisetAmmattitaitovaatimukset2019Dto();
                ot.setKohde(LokalisoituTekstiDto.of("otsikko1-" + id));
                PaikallinenAmmattitaitovaatimus2019Dto yot = new PaikallinenAmmattitaitovaatimus2019Dto();
                yot.setVaatimus(LokalisoituTekstiDto.of("selite1-" + id));
                ot.getVaatimukset().add(yot);
                paikallinen.setOsaamistavoitteet(ot);
            }
            return paikallinen;
        };

        assertThat(uusi).isNotNull();

        { // Adding osa alueet
            uusi.getOsaAlueet().add(createOsaalue.apply("a"));
            uusi.getOsaAlueet().add(createOsaalue.apply("b"));
            uusi.getOsaAlueet().add(createOsaalue.apply("c"));

            sisaltoViiteService.updateSisaltoViite(
                    ops.getKoulutustoimija().getId(),
                    ops.getId(),
                    uusi.getId(),
                    uusi);

            SisaltoViiteDto.Matala updated = sisaltoViiteService.getSisaltoViite(
                    ops.getKoulutustoimija().getId(),
                    ops.getId(),
                    uusi.getId());

            assertThat(updated.getOsaAlueet()).hasSize(3);
            assertThat(updated.getOsaAlueet().get(0).getNimi().get(Kieli.FI)).isEqualTo("nimi-a");
            assertThat(updated.getOsaAlueet().get(1).getNimi().get(Kieli.FI)).isEqualTo("nimi-b");
            assertThat(updated.getOsaAlueet().get(2).getNimi().get(Kieli.FI)).isEqualTo("nimi-c");

            assertThat(updated.getOsaAlueet().get(0).getToteutukset()).hasSize(1);
            assertThat(updated.getOsaAlueet().get(0).getToteutukset().get(0).getArvioinnista().getTeksti().get(Kieli.FI)).isEqualTo("arvioinnista-a");
            assertThat(updated.getOsaAlueet().get(0).getToteutukset().get(0).getTavatjaymparisto().getTeksti().get(Kieli.FI)).isEqualTo("tavatjaymparisto-a");;

        }

        { // Editing osa alueet
            SisaltoViiteDto.Matala updated = sisaltoViiteService.getSisaltoViite(
                    ops.getKoulutustoimija().getId(),
                    ops.getId(),
                    uusi.getId());

            Collections.swap(updated.getOsaAlueet(), 0, 1);

            assertThat(updated.getOsaAlueet()).hasSize(3);
            assertThat(updated.getOsaAlueet().get(0).getNimi().get(Kieli.FI)).isEqualTo("nimi-b");
            assertThat(updated.getOsaAlueet().get(1).getNimi().get(Kieli.FI)).isEqualTo("nimi-a");
            assertThat(updated.getOsaAlueet().get(2).getNimi().get(Kieli.FI)).isEqualTo("nimi-c");
        }
    }

    @Test
    @Rollback
    public void testGetSisaltoviitteetWithQuery() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops1 = createOpetussuunnitelma();
        OpetussuunnitelmaBaseDto ops2 = createOpetussuunnitelma();

        // Lisää toteutussuunnitelmaan paikallisen osan annetulla koodilla
        Function<Object[], SisaltoViiteDto.Matala> addOsaWithKoodi = (Object[] params) -> {
            SisaltoViiteKevytDto tutkinnonosat = getFirstOfType(getKoulutustoimijaId(), (Long) params[0], SisaltoTyyppi.TUTKINNONOSAT);
            return sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), (Long) params[0], tutkinnonosat.getId(), createSisalto(osa -> {
                osa.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
                TutkinnonosaDto tosa = new TutkinnonosaDto();
                tosa.setTyyppi(TutkinnonosaTyyppi.OMA);
                OmaTutkinnonosaDto oma = new OmaTutkinnonosaDto();
                tosa.setOmatutkinnonosa(oma);
                osa.setTosa(tosa);

                LokalisoituTekstiDto teksti = LokalisoituTekstiDto.of((String) params[1]);
                if (params.length == 3) {
                    teksti.getTekstit().put(Kieli.SV, (String) params[2]);
                }

                osa.setTekstiKappale(new TekstiKappaleDto(teksti, null, null));
            }));
        };

        addOsaWithKoodi.apply(new Object[]{ops1.getId(), "tutkinnonosa_11", "sverige_11"});
        addOsaWithKoodi.apply(new Object[]{ops1.getId(), "tutkinnonosa_12", "sverige_12"});

        addOsaWithKoodi.apply(new Object[]{ops2.getId(), "tutkinnonosa_21"});
        addOsaWithKoodi.apply(new Object[]{ops2.getId(), "tutkinnonosa_22"});

        SisaltoviiteQueryDto query = new SisaltoviiteQueryDto();
        query.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);

        {
            Page<SisaltoviiteLaajaDto> page = service.getSisaltoviitteetWithQuery(
                    this.getKoulutustoimijaId(),
                    query,
                    SisaltoviiteLaajaDto.class);
            assertThat(page.getContent()).hasSize(24);
        }

        {
            query.setNimi("tutkinnonosa");
            Page<SisaltoviiteLaajaDto> page = service.getSisaltoviitteetWithQuery(
                    this.getKoulutustoimijaId(),
                    query,
                    SisaltoviiteLaajaDto.class);
            assertThat(page.getContent()).hasSize(4);
            assertThat(page.getContent().get(0).getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("tutkinnonosa_11");
        }

        {
            query.setSortDesc(true);
            Page<SisaltoviiteLaajaDto> page = service.getSisaltoviitteetWithQuery(
                    this.getKoulutustoimijaId(),
                    query,
                    SisaltoviiteLaajaDto.class);
            assertThat(page.getContent()).hasSize(4);
            assertThat(page.getContent().get(0).getTekstiKappale().getNimi().get(Kieli.FI)).isEqualTo("tutkinnonosa_22");
        }

        {
            query.setOpetussuunnitelmaId(ops1.getId());
            Page<SisaltoviiteLaajaDto> page = service.getSisaltoviitteetWithQuery(
                    this.getKoulutustoimijaId(),
                    query,
                    SisaltoviiteLaajaDto.class);

            assertThat(page.getContent()).hasSize(2);
        }

        {
            query.setNimi(null);
            query.setKieli("sv");
            Page<SisaltoviiteLaajaDto> page = service.getSisaltoviitteetWithQuery(
                    this.getKoulutustoimijaId(),
                    query,
                    SisaltoviiteLaajaDto.class);

            assertThat(page.getContent()).hasSize(12);
        }

        {
            query.setNimi("sverige");
            Page<SisaltoviiteLaajaDto> page = service.getSisaltoviitteetWithQuery(
                    this.getKoulutustoimijaId(),
                    query,
                    SisaltoviiteLaajaDto.class);

            assertThat(page.getContent()).hasSize(2);
        }

        {
            query.setNimi(null);
            Page<SisaltoviiteLaajaDto> page = service.getSisaltoviitteetWithQuery(
                    this.getKoulutustoimijaId(),
                    query,
                    SisaltoviiteLaajaDto.class);

            assertThat(page.getContent()).hasSize(12);
        }
    }


    /**
     * Perusteella on kolme suorituspolkua A, B ja C. Paikallisesta opsista on poistettu suorituspolku B. Julkaisuun
     * pitää tulla ops jolla vain A ja C suorituspolut.
     */
    @Test
    @Rollback
    public void testSuorituspolunFiltterointi_EP2977() {
        UUID suorituspolkuA = UUID.randomUUID();
        UUID suorituspolkuB = UUID.randomUUID();
        UUID suorituspolkuC = UUID.randomUUID();

        long suorituspolunSisaltoviiteId = 123L;

        RakenneModuuliDto perusteenRakenneRoot = new RakenneModuuliDto();

        addModuleToRoot(suorituspolkuA,  perusteenRakenneRoot);
        addModuleToRoot(suorituspolkuB,  perusteenRakenneRoot);
        addModuleToRoot(suorituspolkuC,  perusteenRakenneRoot);

        SisaltoViiteDto paikallisestiPoistettavaSisaltoviite = createPaikallisestiPoistettavaSisaltoviite(suorituspolkuB, suorituspolunSisaltoviiteId);

        SuorituspolkuRakenneDto result = sisaltoViiteService.luoSuorituspolkuRakenne(perusteenRakenneRoot, paikallisestiPoistettavaSisaltoviite);

        assertThat(result.getOsat()).extracting(AbstractRakenneOsaDto::getTunniste)
                .containsExactly(suorituspolkuA, suorituspolkuC);

        assertThat(result.getSisaltoviiteId())
                .withFailMessage("Pitäisi olla sisältöviiteId jotta sen avulla julkaisudatasta löydetään oikean suorituspolun tiedot")
                .isEqualTo(suorituspolunSisaltoviiteId);
    }

    @Test
    @Rollback
    public void testAddSisaltoViite_vaaraTyyppiKoulutustyypille() {

        useProfileKP2();
        Opetussuunnitelma ops = createOpetussuunnitelmaJulkaistu();
        liiteService.add(getKoulutustoimijaId(), ops.getId(), "txt", "teksti.txt", 1l, new ByteArrayInputStream("tekstia".getBytes()));

        SisaltoViite opsRoot = sisaltoviiteRepository.findOneRoot(ops);
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());

        sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        }));

        assertThatThrownBy(() ->
                sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto(sisaltoViiteDto -> {
                    sisaltoViiteDto.setTyyppi(SisaltoTyyppi.OPINTOKOKONAISUUS);
                }))).hasMessage("ei-sallittu-sisaltoviite-tyyppi");

    }

    private void addModuleToRoot(UUID tunniste, RakenneModuuliDto moduuliDtoRoot) {
        RakenneModuuliDto moduuliDto = new RakenneModuuliDto();
        moduuliDto.setTunniste(tunniste);
        moduuliDtoRoot.getOsat().add(moduuliDto);
    }

    private SisaltoViiteDto createPaikallisestiPoistettavaSisaltoviite(UUID suorituspolkuTunniste, long poistettavaSisaltoviiteId) {
        SuorituspolkuRiviDto rivi = new SuorituspolkuRiviDto();
        rivi.setRakennemoduuli(suorituspolkuTunniste);
        rivi.setPiilotettu(true);

        SuorituspolkuDto suorituspolku = new SuorituspolkuDto();
        suorituspolku.setRivit(Collections.singleton(rivi));

        SisaltoViiteDto viite = new SisaltoViiteDto();
        viite.setId(poistettavaSisaltoviiteId);
        viite.setSuorituspolku(suorituspolku);

        return viite;
    }
}

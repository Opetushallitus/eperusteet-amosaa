package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.*;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;

@DirtiesContext
@Transactional
public class SisaltoViiteServiceIT extends AbstractIntegrationTest {

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private SisaltoViiteService service;

    @Autowired
    private DtoMapper mapper;

    @Test
    @Rollback
    public void testVierasOsaPerusteesta() {
        useProfileKP3();

        // Ops
        OpetussuunnitelmaDto ops = new OpetussuunnitelmaDto();
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


        Opetussuunnitelma uusi = mapper.map(opetussuunnitelmaService.addOpetussuunnitelma(getKoulutustoimijaId(), ops), Opetussuunnitelma.class);
        Long ktId = getKoulutustoimijaId();
        Long opsId = uusi.getId();

        List<SisaltoViiteKevytDto> sisaltoViitteet = service.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
        SisaltoViiteKevytDto tutkinnonOsat = sisaltoViitteet.stream()
                .filter(sv -> sv.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT))
                .findAny().get();

        SisaltoViiteDto.Matala uusiViite = sisaltoViiteService.addSisaltoViite(ktId, opsId, tutkinnonOsat.getId(), viiteDto);
        assertThat(uusiViite).isNotNull();
    }
    @Test
    @Rollback
    public void testTutkinnonOsanToteutuksenVapaitaTeksteja() {
        useProfileKP3();

        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();

        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto());

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
                .extracting("tosa.toteutukset")
                .containsExactly(toteutukset);

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
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto());
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
        assertThat(catchThrowable(() -> {
                    sisaltoViiteService.removeSisaltoViite(getKoulutustoimijaId(), ops.getId(), added.getId());
                })).isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Sisällöllä on lapsia, ei voida poistaa"); // FIXME: lokalisoi

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
    public void testOpsinSissallonUudelleenjarjestaminen() {
        useProfileKP2();
        OpetussuunnitelmaBaseDto ops = createOpetussuunnitelma();
        SisaltoViiteDto.Matala root = sisaltoViiteService.getSisaltoRoot(getKoulutustoimijaId(), ops.getId());
        SisaltoViiteDto.Matala added = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), root.getId(), createSisalto());
        SisaltoViiteRakenneDto rakenne = sisaltoViiteService.getRakenne(getKoulutustoimijaId(), ops.getId());
        assertThat(rakenne.getLapset().get(0)).hasFieldOrPropertyWithValue("id", added.getId());

        int lastIdx = rakenne.getLapset().size() - 1;
        SisaltoViiteRakenneDto a = rakenne.getLapset().get(0);
        SisaltoViiteRakenneDto b = rakenne.getLapset().get(lastIdx);
        rakenne.getLapset().set(0, b);
        rakenne.getLapset().set(lastIdx, a);
        sisaltoViiteService.reorderSubTree(getKoulutustoimijaId(), ops.getId(), root.getId(), rakenne);
        rakenne = sisaltoViiteService.getRakenne(getKoulutustoimijaId(), ops.getId());
        assertThat(rakenne.getLapset().get(0)).hasFieldOrPropertyWithValue("id", b.getId());
        assertThat(rakenne.getLapset().get(lastIdx)).hasFieldOrPropertyWithValue("id", added.getId());
    }

}

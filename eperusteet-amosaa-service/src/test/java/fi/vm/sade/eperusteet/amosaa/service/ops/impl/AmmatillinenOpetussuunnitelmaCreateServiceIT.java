package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlue;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaTutkinnonosaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TutkinnonosaDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaCreateService;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaDispatcher;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class AmmatillinenOpetussuunnitelmaCreateServiceIT extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TutkinnonosaRepository tutkinnonosaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Test
    public void testOpetussuunnitelmallaTuoreempiPeruste() {
        OpetussuunnitelmaBaseDto pohjaDto = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(7720L);
            opsLuonti.setSuoritustapa("reformi");
        });
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.findOne(pohjaDto.getId());

        SisaltoViiteKevytDto sisaltoviiteTutkinnonOsat = getFirstOfType(getKoulutustoimijaId(), pohja.getId(), SisaltoTyyppi.TUTKINNONOSAT);
        List<SisaltoViiteKevytDto> pohjanTutkinnonosat = getType(getKoulutustoimijaId(), pohja.getId(), SisaltoTyyppi.TUTKINNONOSA);
        assertThat(pohjanTutkinnonosat).hasSize(2);
        SisaltoViite svTosa = sisaltoviiteRepository.findOne(pohjanTutkinnonosat.stream().filter(sv -> sv.getOsaAlueet().size() == 0).findFirst().get().getId());
        svTosa.getTosa().setOsaamisenOsoittaminen(LokalisoituTeksti.of(Kieli.FI, "osaamisenosoittaminen"));
        svTosa.setTekstiKappale(new TekstiKappale());
        svTosa.getTekstiKappale().setTeksti(LokalisoituTeksti.of(Kieli.FI, "tutkinnonosa kuvaus"));
        TutkinnonosaToteutus tutkinnonosaToteutus = new TutkinnonosaToteutus();
        tutkinnonosaToteutus.setKoodit(Set.of("1234"));
        tutkinnonosaToteutus.setTutkinnonosa(svTosa.getTosa());
        svTosa.getTosa().getToteutukset().add(tutkinnonosaToteutus);
        sisaltoviiteRepository.save(svTosa);

        SisaltoViite osaalueSisaltoviite = sisaltoviiteRepository.findOne(pohjanTutkinnonosat.stream().filter(sv -> sv.getOsaAlueet().size() > 0).findFirst().get().getId());

        osaalueSisaltoviite.getOsaAlueet().get(0).setLaajuus(99);

        OmaOsaAlue paikallinenOsaAlue = new OmaOsaAlue();
        paikallinenOsaAlue.setNimi(LokalisoituTeksti.of(Kieli.FI,"paikallinen"));
        paikallinenOsaAlue.setTyyppi(OmaOsaAlueTyyppi.PAIKALLINEN);
        OmaOsaAlueToteutus toteutus = new OmaOsaAlueToteutus();
        toteutus.setOtsikko(LokalisoituTeksti.of(Kieli.FI, "otsikko"));
        paikallinenOsaAlue.getToteutukset().add(toteutus);
        osaalueSisaltoviite.getOsaAlueet().add(paikallinenOsaAlue);

        SisaltoViiteDto.Matala tekstikappale1 = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), pohja.getId(), pohja.getSisaltoviitteet().get(0).getId(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        }));

        SisaltoViiteDto.Matala tekstikappale2 = sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), pohja.getId(), tekstikappale1.getId(), createSisalto(sisaltoViiteDto -> {
            sisaltoViiteDto.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        }));


        // Lisää toteutussuunnitelmaan paikallisen osan annetulla koodilla
        BiFunction<String, Opetussuunnitelma, SisaltoViiteDto.Matala> addOsaWithKoodi = (String koodi, Opetussuunnitelma ops) ->
                sisaltoViiteService.addSisaltoViite(getKoulutustoimijaId(), ops.getId(), sisaltoviiteTutkinnonOsat.getId(), createSisalto(osa -> {
                    osa.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
                    TutkinnonosaDto tosa = new TutkinnonosaDto();
                    tosa.setTyyppi(TutkinnonosaTyyppi.OMA);
                    OmaTutkinnonosaDto oma = new OmaTutkinnonosaDto();
                    oma.setKoodi(koodi);
                    tosa.setOmatutkinnonosa(oma);
                    osa.setTosa(tosa);
                }));

        addOsaWithKoodi.apply("999", pohja);
        addOsaWithKoodi.apply("999999999", pohja);

        OpetussuunnitelmaLuontiDto ops = new OpetussuunnitelmaLuontiDto();
        ops.setOpsId(pohja.getId());
        ops.setNimi(LokalisoituTekstiDto.of("nimi"));
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSTUTKINTO);

        OpetussuunnitelmaBaseDto uudempiOps = dispatcher.get(ops.getKoulutustyyppi(), OpetussuunnitelmaCreateService.class).create(koulutustoimija(), ops);
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(uudempiOps.getId());

        List<SisaltoViiteKevytDto> tutkinnonosat = getType(getKoulutustoimijaId(), uudempiOps.getId(), SisaltoTyyppi.TUTKINNONOSA);
        assertThat(tutkinnonosat.stream().filter(tosa -> tosa.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA)).collect(Collectors.toList())).hasSize(3);
        assertThat(tutkinnonosat.stream().filter(tosa -> tosa.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.OMA)).collect(Collectors.toList())).hasSize(2);

        List<SisaltoViite> sisaltoviitteet = sisaltoviiteRepository.findAllByOwner(opetussuunnitelma);
        SisaltoViite perusteenTosa = sisaltoviitteet.stream()
                .filter(sv -> sv.getTosa() != null && sv.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA) && sv.getTosa().getOsaamisenOsoittaminen() != null).findFirst().get();
        assertThat(perusteenTosa.getTosa().getOsaamisenOsoittaminen().getTeksti().get(Kieli.FI)).isEqualTo(svTosa.getTosa().getOsaamisenOsoittaminen().getTeksti().get(Kieli.FI));
        assertThat(perusteenTosa.getTekstiKappale()).isNotNull();
        assertThat(perusteenTosa.getTekstiKappale().getTeksti().getTeksti().get(Kieli.FI)).isEqualTo("tutkinnonosa kuvaus");

        SisaltoViite osaalueellinen = sisaltoviiteRepository.findOne(tutkinnonosat.stream()
                .filter(tosa -> tosa.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA) && tosa.getOsaAlueet().size() > 0).findFirst().get().getId());

        assertThat(osaalueellinen.getOsaAlueet()).hasSize(3);
        assertThat(osaalueellinen.getOsaAlueet().stream().filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN)).collect(Collectors.toList())).hasSize(1);
        assertThat(osaalueellinen.getOsaAlueet().stream().filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN)).collect(Collectors.toList())).hasSize(1);
        assertThat(osaalueellinen.getOsaAlueet().stream().filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN)).collect(Collectors.toList())).hasSize(1);

        List<SisaltoViiteKevytDto> tekstikappaleet = getType(getKoulutustoimijaId(), uudempiOps.getId(), SisaltoTyyppi.TEKSTIKAPPALE);
        assertThat(tekstikappaleet).hasSize(3);
    }
}

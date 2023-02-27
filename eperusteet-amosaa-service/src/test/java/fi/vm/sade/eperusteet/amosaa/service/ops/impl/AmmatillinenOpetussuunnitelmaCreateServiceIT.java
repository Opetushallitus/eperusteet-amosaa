package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
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
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
            opsLuonti.setPerusteId(4489213L);
        });
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.findOne(pohjaDto.getId());

        SisaltoViiteKevytDto sisaltoviiteTutkinnonOsat = getFirstOfType(getKoulutustoimijaId(), pohja.getId(), SisaltoTyyppi.TUTKINNONOSAT);
        List<SisaltoViiteKevytDto> pohjanTutkinnonosat = getType(getKoulutustoimijaId(), pohja.getId(), SisaltoTyyppi.TUTKINNONOSA);
        assertThat(pohjanTutkinnonosat).hasSize(1);
        SisaltoViite svTosa = sisaltoviiteRepository.findOne(pohjanTutkinnonosat.get(0).getId());
        svTosa.getTosa().setOsaamisenOsoittaminen(LokalisoituTeksti.of(Kieli.FI, "osaamisenosoittaminen"));
        TutkinnonosaToteutus tutkinnonosaToteutus = new TutkinnonosaToteutus();
        tutkinnonosaToteutus.setKoodit(Set.of("1234"));
        tutkinnonosaToteutus.setTutkinnonosa(svTosa.getTosa());
        svTosa.getTosa().getToteutukset().add(tutkinnonosaToteutus);
        sisaltoviiteRepository.save(svTosa);

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
        ops.setKoulutustyyppi(KoulutusTyyppi.PERUSTUTKINTO);

        OpetussuunnitelmaBaseDto dispatchedOps = dispatcher.get(ops.getKoulutustyyppi(), OpetussuunnitelmaCreateService.class).create(koulutustoimija(), ops);

        List<SisaltoViiteKevytDto> tutkinnonosat = getType(getKoulutustoimijaId(), dispatchedOps.getId(), SisaltoTyyppi.TUTKINNONOSA);
        assertThat(tutkinnonosat.stream().filter(tosa -> tosa.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA)).collect(Collectors.toList())).hasSize(1);
        assertThat(tutkinnonosat.stream().filter(tosa -> tosa.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.OMA)).collect(Collectors.toList())).hasSize(2);

        SisaltoViite perusteenTosa = sisaltoviiteRepository.findOne(tutkinnonosat.stream().filter(tosa -> tosa.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA)).findFirst().get().getId());
        assertThat(perusteenTosa.getTosa().getOsaamisenOsoittaminen().getTeksti().get(Kieli.FI)).isEqualTo(svTosa.getTosa().getOsaamisenOsoittaminen().getTeksti().get(Kieli.FI));
        assertThat(perusteenTosa.getTosa().getPerusteentutkinnonosa()).isNotEqualTo(svTosa.getTosa().getPerusteentutkinnonosa());
    }
}

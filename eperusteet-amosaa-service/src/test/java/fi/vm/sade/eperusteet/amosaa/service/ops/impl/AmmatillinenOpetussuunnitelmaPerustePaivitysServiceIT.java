package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueKokonaanDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Osaamistavoite2020Dto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaDispatcher;
import fi.vm.sade.eperusteet.amosaa.service.peruste.OpetussuunnitelmaPerustePaivitysService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class AmmatillinenOpetussuunnitelmaPerustePaivitysServiceIT extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TutkinnonosaRepository tutkinnonosaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private EperusteetClient eperusteetClient;

    @Test
    public void testOpetussuunnitelmaOsaALuePoistettu() {
        OpetussuunnitelmaBaseDto opsDto = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(8492L);
            opsLuonti.setSuoritustapa("reformi");
        });
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsDto.getId());
        tarkistaLukumaarat(ops, 2, 4);

        PerusteKaikkiDto perusteDto = eperusteetClient.getPeruste(ops.getPeruste().getPerusteId(), PerusteKaikkiDto.class);
        TutkinnonosaKaikkiDto osaalueellinen = perusteDto.getTutkinnonOsat().stream().filter(tosa -> !tosa.getOsaAlueet().isEmpty()).findFirst().get();
        osaalueellinen.getOsaAlueet().remove(0);

        dispatcher.get(ops.getOpsKoulutustyyppi(), OpetussuunnitelmaPerustePaivitysService.class).paivitaOpetussuunnitelma(opsDto.getId(), perusteDto);
        tarkistaLukumaarat(ops, 2, 2);
    }

    @Test
    public void testOpetussuunnitelmaOsaALueLisatty() {
        OpetussuunnitelmaBaseDto opsDto = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(8492L);
            opsLuonti.setSuoritustapa("reformi");
        });

        lisaaPaikallinenOsaAlue(opsDto);

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsDto.getId());
        tarkistaLukumaarat(ops, 2, 5);

        PerusteKaikkiDto perusteDto = eperusteetClient.getPeruste(ops.getPeruste().getPerusteId(), PerusteKaikkiDto.class);
        TutkinnonosaKaikkiDto osaalueellinen = perusteDto.getTutkinnonOsat().stream().filter(tosa -> !tosa.getOsaAlueet().isEmpty()).findFirst().get();
        osaalueellinen.getOsaAlueet().add(buildOsaAlue());

        dispatcher.get(ops.getOpsKoulutustyyppi(), OpetussuunnitelmaPerustePaivitysService.class).paivitaOpetussuunnitelma(opsDto.getId(), perusteDto);
        tarkistaLukumaarat(ops, 2, 6);
    }

    private void lisaaPaikallinenOsaAlue(OpetussuunnitelmaBaseDto opsDto) {
        List<SisaltoViiteKevytDto> tutkinnonosat = getType(getKoulutustoimijaId(), opsDto.getId(), SisaltoTyyppi.TUTKINNONOSA);
        SisaltoViiteKevytDto sisaltoviite = tutkinnonosat.stream().filter(tosa -> tosa.getOsaAlueet().size() > 0)
                .findFirst()
                .orElse(new SisaltoViiteKevytDto());
        SisaltoViiteDto.Matala sisaltoviitedto = sisaltoViiteService.getSisaltoViite(getKoulutustoimijaId(), opsDto.getId(), sisaltoviite.getId());
        sisaltoviitedto.getOsaAlueet().add(OmaOsaAlueDto
                .builder()
                .tyyppi(OmaOsaAlueTyyppi.PAIKALLINEN)
                .nimi(LokalisoituTekstiDto.of("paikallinen"))
                .build());
        sisaltoViiteService.updateSisaltoViite(getKoulutustoimijaId(), opsDto.getId(), sisaltoviite.getId(), sisaltoviitedto);
    }

    @Test
    public void testOpetussuunnitelmaTutkinnonosaLisatty() {
        OpetussuunnitelmaBaseDto opsDto = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(8492L);
            opsLuonti.setSuoritustapa("reformi");
        });
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsDto.getId());
        tarkistaLukumaarat(ops, 2, 4);

        PerusteKaikkiDto perusteDto = eperusteetClient.getPeruste(ops.getPeruste().getPerusteId(), PerusteKaikkiDto.class);
        perusteDto.getTutkinnonOsat().add(buildTutkinnonosaKaikkiDto());

        dispatcher.get(ops.getOpsKoulutustyyppi(), OpetussuunnitelmaPerustePaivitysService.class).paivitaOpetussuunnitelma(opsDto.getId(), perusteDto);
        tarkistaLukumaarat(ops, 3, 4);
    }

    @Test
    public void testOpetussuunnitelmaTutkinnonosaPoistettu() {
        OpetussuunnitelmaBaseDto opsDto = createOpetussuunnitelma(opsLuonti -> {
            opsLuonti.setPerusteId(8492L);
            opsLuonti.setSuoritustapa("reformi");
        });
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsDto.getId());
        tarkistaLukumaarat(ops, 2, 4);

        PerusteKaikkiDto perusteDto = eperusteetClient.getPeruste(ops.getPeruste().getPerusteId(), PerusteKaikkiDto.class);
        TutkinnonosaKaikkiDto osaalueellinen = perusteDto.getTutkinnonOsat().stream().filter(tosa -> !tosa.getOsaAlueet().isEmpty()).findFirst().get();
        perusteDto.getTutkinnonOsat().remove(osaalueellinen);

        dispatcher.get(ops.getOpsKoulutustyyppi(), OpetussuunnitelmaPerustePaivitysService.class).paivitaOpetussuunnitelma(opsDto.getId(), perusteDto);
        tarkistaLukumaarat(ops, 1, 0);
    }

    private void tarkistaLukumaarat(Opetussuunnitelma ops, int tosaLkm, int osaALueLkm) {
        List<SisaltoViiteKevytDto> tutkinnonosat = getType(getKoulutustoimijaId(), ops.getId(), SisaltoTyyppi.TUTKINNONOSA);
        assertThat(tutkinnonosat).hasSize(tosaLkm);

        assertThat(tutkinnonosat.stream().filter(tosa -> tosa.getOsaAlueet().size() > 0).findFirst().orElse(new SisaltoViiteKevytDto()).getOsaAlueet()).hasSize(osaALueLkm);
    }

    private OsaAlueKokonaanDto buildOsaAlue() {
        OsaAlueKokonaanDto oa = new OsaAlueKokonaanDto();
        oa.setNimi(LokalisoituTekstiDto.of("test"));
        oa.setKoodiUri("osaalue_123");
        oa.setId(3131313L);
        oa.setPakollisetOsaamistavoitteet(new Osaamistavoite2020Dto());
        return oa;
    }

    private TutkinnonosaKaikkiDto buildTutkinnonosaKaikkiDto() {
        TutkinnonosaKaikkiDto tosa = new TutkinnonosaKaikkiDto();
        tosa.setId(9999L);
        tosa.setKoodiUri("tosakoodi_12321");
        tosa.setNimi(LokalisoituTekstiDto.of("tosanimi"));
        tosa.setOsaAlueet(new ArrayList<>());
        return tosa;
    }
}

package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.dto.OrganisaatioHierarkia;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class KoulutustoimijaServiceIT extends AbstractIntegrationTest {

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Test
    @Rollback
    public void testKoulutustoimijoidenLuonti() {
        boolean x = kayttajanTietoService.updateKoulutustoimijat();
        assertThat(kayttaja).isNotNull();
        assertThat(koulutustoimijat).isNotEmpty();
    }

    @Test
    @Rollback
    public void testOrganisaatiohierarkiaYstavatKP1() {
        List<KoulutustoimijaYstavaDto> organisaatioHierarkiaYstavat = koulutustoimijaService.getOrganisaatioHierarkiaYstavat(getKoulutustoimijaId());
        assertThat(organisaatioHierarkiaYstavat).hasSize(0);
        useProfileKP1();
        List<KoulutustoimijaYstavaDto> orgs = koulutustoimijaService.getOrganisaatioHierarkiaYstavat(getKoulutustoimijaId());
        assertThat(orgs.stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactlyInAnyOrder("1.2.246.562.10.2013120512391252668625");
        useProfileKP3();
        useProfileKP1();
        orgs = koulutustoimijaService.getOrganisaatioHierarkiaYstavat(getKoulutustoimijaId());
        assertThat(orgs.stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactlyInAnyOrder("1.2.246.562.10.2013120512391252668625", "1.2.246.562.10.2013120513110198396408");
    }

    @Test
    @Rollback
    public void testOrganisaatiohierarkiaYstavatKP2() {
        useProfileKP1();
        useProfileKP2();
        List<KoulutustoimijaYstavaDto> orgs = koulutustoimijaService.getOrganisaatioHierarkiaYstavat(getKoulutustoimijaId());
        assertThat(orgs.stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactlyInAnyOrder("1.2.246.562.10.54645809036");
    }

    @Test
    @Rollback
    public void testOrganisaatiohierarkiaYstavatKP3() {
        useProfileKP1();
        useProfileKP2();
        useProfileKP3();
        List<KoulutustoimijaYstavaDto> orgs = koulutustoimijaService.getOrganisaatioHierarkiaYstavat(getKoulutustoimijaId());
        assertThat(orgs.stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactlyInAnyOrder("1.2.246.562.10.54645809036", "1.2.246.562.10.2013120512391252668625");
        assertThat(koulutustoimijaService.getOrganisaatioHierarkiaYstavat(getKoulutustoimijaId())).hasSize(2);
    }

    @Test
    @Rollback
    public void testKoulutustoimijaYstavat2ja3() {
        // Palveluun rekisteröityneenä organisaatiotasot 2 ja 3
        useProfileKP2();
        assertThat(koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .isEmpty();
        useProfileKP3();
        assertThat(koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactly("1.2.246.562.10.2013120512391252668625");

        useProfileKP2();
        assertThat(koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactly("1.2.246.562.10.2013120513110198396408");
    }

    @Test
    @Rollback
    public void testKoulutustoimijaYstavat1_2_3() {
        useProfileKP1();
        HashSet<KoulutustoimijaBaseDto> asd = new HashSet<>();
        asd.add(getKoulutustoimija());
        useProfileKP2();
        asd.add(getKoulutustoimija());
        useProfileKP3();
        asd.add(getKoulutustoimija());

        assertThat(koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactlyInAnyOrder("1.2.246.562.10.2013120512391252668625", "1.2.246.562.10.54645809036");

        useProfileKP2();
        assertThat(koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId()).stream().map(KoulutustoimijaYstavaDto::getOrganisaatio))
                .containsExactlyInAnyOrder("1.2.246.562.10.2013120513110198396408", "1.2.246.562.10.54645809036");
    }

    @Test
    @Rollback
    public void testKoulutustoimijaYstavatYksiKayttaja() {
        Long id = koulutustoimijat.get(0).getId();
        List<KoulutustoimijaYstavaDto> ystavat = koulutustoimijaService.getOmatYstavat(id);
        assertThat(ystavat).hasSize(0);
    }

    @Test
    @Rollback
    public void testKoulutustoimijaYstavaPyynnot() {
        assertThat(koulutustoimijaService.getPyynnot(getKoulutustoimijaId())).isEmpty();
    }

    @Test
    @Rollback
    public void testKoulutustoimijaHaku() {
        PageRequest p = new PageRequest(0, 10);
        KoulutustoimijaQueryDto pquery = new KoulutustoimijaQueryDto();
        Page<KoulutustoimijaJulkinenDto> julkisetToimijat = koulutustoimijaService.findKoulutustoimijat(p, pquery);
        assertThat(julkisetToimijat).isEmpty();

        assertThat(koulutustoimijaService.getKoulutustoimijaJulkinen(getKoulutustoimijaId()))
                .hasFieldOrPropertyWithValue("id", getKoulutustoimijaId());
        assertThat(koulutustoimijaService.getKoulutustoimijaJulkinen(getKoulutustoimija().getOrganisaatio()))
                .hasFieldOrPropertyWithValue("id", getKoulutustoimijaId());
        assertThat(koulutustoimijaService.getPaikallisetTutkinnonOsat(getKoulutustoimijaId(), SisaltoViiteDto.class))
                .isEmpty();
    }

}

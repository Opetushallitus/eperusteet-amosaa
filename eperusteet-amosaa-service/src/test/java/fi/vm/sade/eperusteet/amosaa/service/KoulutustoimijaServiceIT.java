package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class KoulutustoimijaServiceIT extends AbstractIntegrationTest {
   
    @Test
    @Rollback
    public void testKoulutustoimijoidenLuonti() {
        kayttajanTietoService.updateKoulutustoimijat();
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

    @Test
    @Rollback
    public void testKoulutustoimijaHakuKielirajaus() {
        kayttajanTietoService.updateKoulutustoimijat();

        {
            OpetussuunnitelmaBaseDto dto = createOpetussuunnitelma();

            OpetussuunnitelmaDto ops = opetussuunnitelmaService.getOpetussuunnitelma(getKoulutustoimijaId(), dto.getId());

            Set<Kieli> kielet = new HashSet<>();
            kielet.add(Kieli.FI);
            ops.setJulkaisukielet(kielet);

            // Validointi vaatii
            ops.setHyvaksyja("Hyvaksyja");
            ops.setPaatosnumero("Paatosnumero");
            ops.setPaatospaivamaara(new Date());

            opetussuunnitelmaService.update(getKoulutustoimijaId(), ops.getId(), ops);
            opetussuunnitelmaService.updateTila(getKoulutustoimijaId(), ops.getId(), Tila.VALMIS, false);
            opetussuunnitelmaService.updateTila(getKoulutustoimijaId(), ops.getId(), Tila.JULKAISTU, false);
        }

        PageRequest p = new PageRequest(0, 10);
        KoulutustoimijaQueryDto pquery = new KoulutustoimijaQueryDto();

        Page<KoulutustoimijaJulkinenDto> julkisetToimijat = koulutustoimijaService.findKoulutustoimijat(p, pquery);
        assertThat(julkisetToimijat).hasSize(1);

        pquery.setKieli(Kieli.EN.toString());
        julkisetToimijat = koulutustoimijaService.findKoulutustoimijat(p, pquery);
        assertThat(julkisetToimijat).isEmpty();
    }
    
    @Test
    @Rollback
    public void testKoulutustoimijaSorttaus() {
    	
    	useProfileKP2();
    	Opetussuunnitelma opetussuunnitelma = createOpetussuunnitelmaJulkaistu();
    	updateOpetussuunnitelmaJulkaisukielet(opetussuunnitelma, Sets.newHashSet(Kieli.FI, Kieli.EN));
    	updateKoulutustoimijaLokalisointiNimet(ImmutableMap.of(Kieli.FI, "testijarjestys5", Kieli.EN, "test1"));
    	
    	useProfileKP1();
    	opetussuunnitelma = createOpetussuunnitelmaJulkaistu();
    	updateOpetussuunnitelmaJulkaisukielet(opetussuunnitelma, Sets.newHashSet(Kieli.FI, Kieli.EN));
    	updateKoulutustoimijaLokalisointiNimet(ImmutableMap.of(Kieli.FI, "testijarjestys1", Kieli.EN, "test2"));
    	
    	useProfileKP3();
    	createOpetussuunnitelmaJulkaistu();    	
    	updateKoulutustoimijaLokalisointiNimet(ImmutableMap.of(Kieli.FI, "testijarjestys3"));
    	
    	PageRequest p = new PageRequest(0, 10);
        KoulutustoimijaQueryDto pquery = new KoulutustoimijaQueryDto();
        
        Page<KoulutustoimijaJulkinenDto> julkisetToimijat = koulutustoimijaService.findKoulutustoimijat(p, pquery);
        assertThat(julkisetToimijat).hasSize(3);
        assertThat(julkisetToimijat.getContent().stream().map(k -> k.getNimi().get(Kieli.FI))).containsExactly("testijarjestys1", "testijarjestys3","testijarjestys5");
        assertThat(julkisetToimijat.getContent().stream().map(k -> k.getNimi().get(Kieli.EN))).containsExactly("test2", null, "test1");
        
        pquery.setKieli(Kieli.EN.toString());

        julkisetToimijat = koulutustoimijaService.findKoulutustoimijat(p, pquery);
        assertThat(julkisetToimijat).hasSize(2);
        assertThat(julkisetToimijat.getContent().stream()
        		.map(k -> k.getNimi().get(Kieli.FI)))
        		.containsExactly("testijarjestys5","testijarjestys1");
        
        assertThat(julkisetToimijat.getContent().stream()
        		.map(k -> k.getNimi().get(Kieli.EN)))
        		.containsExactly("test1", "test2");
    }

}

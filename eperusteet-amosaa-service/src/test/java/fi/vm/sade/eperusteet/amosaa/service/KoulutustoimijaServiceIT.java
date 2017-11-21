package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.dto.OrganisaatioHierarkia;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DirtiesContext
@Transactional
public class KoulutustoimijaServiceIT extends AbstractIntegrationTest {
    @Test
    @Rollback
    public void testKoulutustoimijoidenLuonti() {
        boolean x = kayttajanTietoService.updateKoulutustoimijat();
        assertThat(kayttaja).isNotNull();
        assertThat(koulutustoimijat).isNotEmpty();
    }

    @Test
    @Rollback
    public void testKoulutustoimijaYstavat() {
        useProfileTest1();
        useProfileTest();
        OrganisaatioHierarkia hierarkia = koulutustoimijaService.getOrganisaatioHierarkia(getKoulutustoimijaId());
        List<KoulutustoimijaYstavaDto> ystavat = koulutustoimijaService.getOmatYstavat(getKoulutustoimijaId());
        assertThat(ystavat).hasSize(2);
    }

    @Test
    @Rollback
    public void testKoulutustoimijaYstavatYksiKayttaja() {
        Long id = koulutustoimijat.get(0).getId();
        OrganisaatioHierarkia hierarkia = koulutustoimijaService.getOrganisaatioHierarkia(id);
        List<KoulutustoimijaYstavaDto> ystavat = koulutustoimijaService.getOmatYstavat(id);
        assertThat(ystavat).hasSize(1);
    }


}

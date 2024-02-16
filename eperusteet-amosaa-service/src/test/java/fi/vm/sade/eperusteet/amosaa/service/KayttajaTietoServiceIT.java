package fi.vm.sade.eperusteet.amosaa.service;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaKtoDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@Transactional
public class KayttajaTietoServiceIT extends AbstractIntegrationTest {

    @Autowired
    KayttajanTietoService kayttajanTietoService;

    @Test
    public void testGetKaikkiKayttajat() {

        useProfileKP1();

        List<KayttajaKtoDto> kaikkiKayttajat = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId(), PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA);

        assertThat(kaikkiKayttajat).hasSize(5);
        assertThat(kaikkiKayttajat)
                .extracting("oid").contains(KP1, KP2, KP7_KAYTTAJA_OID, KP8_KAYTTAJA_OID, TMPR);
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP1)).findFirst().get().getId()).isNotNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP1)).findFirst().get().getSukunimi()).isNotNull();

        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP2)).findFirst().get().getId()).isNotNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP2)).findFirst().get().getSukunimi()).isNotNull();

        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP7_KAYTTAJA_OID)).findFirst().get().getId()).isNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP7_KAYTTAJA_OID)).findFirst().get().getSukunimi()).isNotNull();

        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP8_KAYTTAJA_OID)).findFirst().get().getId()).isNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP8_KAYTTAJA_OID)).findFirst().get().getSukunimi()).isNotNull();

        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(TMPR)).findFirst().get().getId()).isNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(TMPR)).findFirst().get().getSukunimi()).isNotNull();

    }

    @Test
    public void testGetKaikkiKayttajat_kayttajaId_loytyy() {

        useProfileKP1();

        List<KayttajaKtoDto> kaikkiKayttajat = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId(), PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA);
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(TMPR)).findFirst().get().getId()).isNull();

        useProfileTmpr();
        useProfileKP1();

        kaikkiKayttajat = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId(), PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA);
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(TMPR)).findFirst().get().getId()).isNotNull();

    }

}

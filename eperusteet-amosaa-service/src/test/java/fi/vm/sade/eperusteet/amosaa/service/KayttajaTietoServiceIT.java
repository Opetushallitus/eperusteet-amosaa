package fi.vm.sade.eperusteet.amosaa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.util.KayttooikeusServiceMock;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;

@DirtiesContext
@Transactional
public class KayttajaTietoServiceIT extends AbstractIntegrationTest {

    @Autowired
    KayttajanTietoService kayttajanTietoService;
    
    @Test
    public void testGetKaikkiKayttajat() {

        useProfileKP1();        

        List<KayttajaDto> kaikkiKayttajat = kayttajanTietoService.getKaikkiKayttajat(getKoulutustoimijaId());

        assertThat(kaikkiKayttajat).hasSize(5);
        assertThat(kaikkiKayttajat)
                .extracting("oid").contains(KP1, KP2, KP7_KAYTTAJA_OID, KP8_KAYTTAJA_OID, TMPR);
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP1)).findFirst().get().getId()).isNotNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP2)).findFirst().get().getId()).isNotNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP7_KAYTTAJA_OID)).findFirst().get().getId()).isNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KP8_KAYTTAJA_OID)).findFirst().get().getId()).isNull();
        
    }

}

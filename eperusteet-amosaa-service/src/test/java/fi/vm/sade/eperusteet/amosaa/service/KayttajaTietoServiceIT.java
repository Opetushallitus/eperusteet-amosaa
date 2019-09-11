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
        
        assertThat(kaikkiKayttajat).hasSize(4);
        assertThat(kaikkiKayttajat)
            .extracting("oid").contains("kp1", "kp2", KayttooikeusServiceMock.KP1_KAYTTAJA_OID, KayttooikeusServiceMock.KP2_KAYTTAJA_OID);
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals("kp1")).findFirst().get().getId()).isNotNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals("kp2")).findFirst().get().getId()).isNotNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KayttooikeusServiceMock.KP1_KAYTTAJA_OID)).findFirst().get().getId()).isNull();
        assertThat(kaikkiKayttajat.stream().filter(k -> k.getOid().equals(KayttooikeusServiceMock.KP2_KAYTTAJA_OID)).findFirst().get().getId()).isNull();
        
    }

}

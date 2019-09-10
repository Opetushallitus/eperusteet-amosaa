package fi.vm.sade.eperusteet.amosaa.service.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import fi.vm.sade.eperusteet.amosaa.service.external.KayttooikeusService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;

@Service
@Profile("test")
public class KayttooikeusServiceMock implements KayttooikeusService{

    public static final String KP1_KAYTTAJA_OID = "1.2.3.4.5.kp1";
    public static final String KP2_KAYTTAJA_OID = "1.22.3.4.5.kp2";
    public static final String KPTMPR_KAYTTAJA_OID = "1.22.3.4.5.TMPR";
    
    @Override
    public List<KayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid) {
        return Optional.ofNullable(KayttooikeusServiceMock.kayttoikeuskayttajaMap().get(organisaatioOid))
                .orElseGet(Collections::emptyList);
    }
    
    private static Map<String, List<KayttajaDto>> kayttoikeuskayttajaMap() {
        return ImmutableMap.of(
                AbstractIntegrationTest.oidKp1, Arrays.asList(kayttajadto(AbstractIntegrationTest.KP1,"kp1sukunimi"),
                        kayttajadto(KP1_KAYTTAJA_OID,"kp1sukunimi")),
                AbstractIntegrationTest.oidKp2, Arrays.asList(
                        kayttajadto(AbstractIntegrationTest.KP2,"kp2sukunimi"),
                        kayttajadto(KP2_KAYTTAJA_OID,"kp2sukunimi"),
                        kayttajadto(KP1_KAYTTAJA_OID,"kp1sukunimi")),
                AbstractIntegrationTest.oidTmpr, Arrays.asList(kayttajadto(KPTMPR_KAYTTAJA_OID,"kpTmprsukunimi")));
    }

    private static KayttajaDto kayttajadto(String oid, String sukunimi) {
        KayttajaDto dto = new KayttajaDto();
        dto.setOid(oid);
        dto.setSukunimi(sukunimi);

        return dto;
    }
}

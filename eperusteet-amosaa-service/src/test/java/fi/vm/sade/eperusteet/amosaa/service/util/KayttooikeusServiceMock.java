package fi.vm.sade.eperusteet.amosaa.service.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttooikeusKayttajaDto;
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
    public List<KayttooikeusKayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid) {
        return Optional.ofNullable(KayttooikeusServiceMock.kayttoikeuskayttajaMap().get(organisaatioOid))
                .orElseGet(Collections::emptyList);
    }

    private static Map<String, List<KayttooikeusKayttajaDto>> kayttoikeuskayttajaMap() {
        return ImmutableMap.of(
                AbstractIntegrationTest.oidKp1, Arrays.asList(
                        KayttooikeusKayttajaDto.builder()
                                .oid(AbstractIntegrationTest.KP1)
                                .sukunimi("kp1sukunimi")
                                .build(),
                        KayttooikeusKayttajaDto.builder()
                                .oid(KP1_KAYTTAJA_OID)
                                .sukunimi("kp1sukunimi")
                                .build()),
                AbstractIntegrationTest.oidKp2, Arrays.asList(
                        KayttooikeusKayttajaDto.builder()
                                .oid(AbstractIntegrationTest.KP2)
                                .sukunimi("kp2sukunimi")
                                .build(),
                        KayttooikeusKayttajaDto.builder()
                                .oid(KP2_KAYTTAJA_OID)
                                .sukunimi("kp2sukunimi")
                                .build(),
                        KayttooikeusKayttajaDto.builder()
                                .oid(KP1_KAYTTAJA_OID)
                                .sukunimi("kp1sukunimi")
                                .build()),
                AbstractIntegrationTest.oidTmpr, Arrays.asList(KayttooikeusKayttajaDto.builder()
                        .oid(KPTMPR_KAYTTAJA_OID)
                        .sukunimi("kpTmprsukunimi")
                        .build()));
    }
}

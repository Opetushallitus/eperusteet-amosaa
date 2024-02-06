package fi.vm.sade.eperusteet.amosaa.service.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttooikeusKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import fi.vm.sade.eperusteet.amosaa.service.external.KayttooikeusService;
import fi.vm.sade.eperusteet.amosaa.test.AbstractIntegrationTest;

@Service
@Profile("test")
public class KayttooikeusServiceMock implements KayttooikeusService{

    @Override
    public List<KayttooikeusKayttajaDto> getOrganisaatioVirkailijat(String organisaatioOid, PermissionEvaluator.RolePrefix rolePrefix) {
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
                                .oid(AbstractIntegrationTest.KP7_KAYTTAJA_OID)
                                .sukunimi("kp7sukunimi")
                                .build()),
                AbstractIntegrationTest.oidKp2, Arrays.asList(
                        KayttooikeusKayttajaDto.builder()
                                .oid(AbstractIntegrationTest.KP2)
                                .sukunimi("kp2sukunimi")
                                .build(),
                        KayttooikeusKayttajaDto.builder()
                                .oid(AbstractIntegrationTest.KP8_KAYTTAJA_OID)
                                .sukunimi("kp8sukunimi")
                                .build(),
                        KayttooikeusKayttajaDto.builder()
                                .oid(AbstractIntegrationTest.KP7_KAYTTAJA_OID)
                                .sukunimi("kp7sukunimi")
                                .build(),
                        KayttooikeusKayttajaDto.builder()
                                .oid(AbstractIntegrationTest.TMPR)
                                .sukunimi("kp1sukunimi")
                                .build()),
                AbstractIntegrationTest.oidTmpr, Arrays.asList(KayttooikeusKayttajaDto.builder()
                        .oid(AbstractIntegrationTest.KPTMPR_KAYTTAJA_OID)
                        .sukunimi("kpTmprsukunimi")
                        .build()));
    }
}

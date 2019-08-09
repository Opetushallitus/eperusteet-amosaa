package fi.vm.sade.eperusteet.amosaa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.assertj.core.api.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.SuoritustapaLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuDto;
import fi.vm.sade.eperusteet.amosaa.service.ops.impl.SisaltoViiteServiceImpl;
import fi.vm.sade.eperusteet.amosaa.service.peruste.PerusteCacheService;
import fi.vm.sade.eperusteet.amosaa.test.TestClassCreatorUtil;

@RunWith(MockitoJUnitRunner.class)
public class SisaltoViiteServiceImplTest {

    private List<UUID> rakennemoduulitunnisteet = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    private List<UUID> rakennemoduulitunnisteetPiilotetut = Arrays.asList(rakennemoduulitunnisteet.get(1),
            rakennemoduulitunnisteet.get(3), rakennemoduulitunnisteet.get(5));

    @Mock
    private PerusteCacheService perusteCacheService;

    @InjectMocks
    private SisaltoViiteServiceImpl sisaltoViiteServiceImpl = new SisaltoViiteServiceImpl();

    @Before
    public void setup() {
        Mockito.when(perusteCacheService.getSuoritustapa(Mockito.any(Opetussuunnitelma.class), Mockito.any(Long.class)))
                .thenReturn(createSuoritustapaLaajaDtoRakenteella());
    }

    @Test
    public void updateOpetussuunnitelmaPiilotetutSisaltoviitteetTest() {

        Opetussuunnitelma opetussuunnitelma = TestClassCreatorUtil.opetussuunnitelma();

        sisaltoViiteServiceImpl.updateOpetussuunnitelmaPiilotetutSisaltoviitteet(createSisaltoViiteDtoWithPiilotukset(),
                opetussuunnitelma);

        assertThat(opetussuunnitelma.getOsaamisalat()).hasSize(1);
        assertThat(opetussuunnitelma.getTutkintonimikkeet()).hasSize(2);
    }

    private SuoritustapaLaajaDto createSuoritustapaLaajaDtoRakenteella() {

        SuoritustapaLaajaDto suoritustapaLaajaDto = new SuoritustapaLaajaDto();

        RakenneModuuliDto rakenne = TestClassCreatorUtil.rakenneModuuliDto(null, null,
                Arrays.asList(
                        TestClassCreatorUtil.rakenneModuuliDtoWithOsaamisalaUri(
                                rakennemoduulitunnisteet.get(0),
                                Arrays.asList(TestClassCreatorUtil
                                        .rakenneModuuliDtoWithTutkimusnimikeUri(rakennemoduulitunnisteet.get(1)))),
                        TestClassCreatorUtil.rakenneModuuliDto(null, null, Arrays.asList(
                                TestClassCreatorUtil
                                        .rakenneModuuliDtoWithTutkimusnimikeUri(rakennemoduulitunnisteet.get(2)),
                                TestClassCreatorUtil
                                        .rakenneModuuliDtoWithTutkimusnimikeUri(rakennemoduulitunnisteet.get(3)),
                                TestClassCreatorUtil.rakenneModuuliDtoWithTutkimusnimikeUri(
                                        rakennemoduulitunnisteet.get(4),
                                        Arrays.asList(TestClassCreatorUtil.rakenneModuuliDtoWithTutkimusnimikeUri(
                                                rakennemoduulitunnisteet.get(5),
                                                Arrays.asList(
                                                        TestClassCreatorUtil.rakenneModuuliDtoWithTutkimusnimikeUri(
                                                                rakennemoduulitunnisteet.get(6)))))))),
                        TestClassCreatorUtil.rakenneModuuliDto())

        );

        suoritustapaLaajaDto.setRakenne(rakenne);

        return suoritustapaLaajaDto;
    }

    private SisaltoViiteDto createSisaltoViiteDtoWithPiilotukset() {

        SisaltoViiteDto sisaltoViiteDto = new SisaltoViiteDto();

        SuorituspolkuDto suorituspolkuDto = new SuorituspolkuDto();
        suorituspolkuDto.setRivit(rakennemoduulitunnisteet.stream()
                .map(r -> SuorituspolkuRiviDto.of(r, rakennemoduulitunnisteetPiilotetut.contains(r), null))
                .collect(Collectors.toSet()));

        sisaltoViiteDto.setSuorituspolku(suorituspolkuDto);

        return sisaltoViiteDto;
    }

}

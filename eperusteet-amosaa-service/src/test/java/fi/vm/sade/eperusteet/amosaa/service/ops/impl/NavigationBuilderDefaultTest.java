package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TutkinnonOsaKevytDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static fi.vm.sade.eperusteet.amosaa.test.TestClassCreatorUtil.opetussuunnitelma;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NavigationBuilderDefaultTest {

    @Mock
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Mock
    private SisaltoViiteService sisaltoViiteService;

    @InjectMocks
    private NavigationBuilderDefault navigationBuilderDefault = new NavigationBuilderDefault();

    @Before
    public void setup() {
        when(opetussuunnitelmaRepository.findOne(anyLong())).thenReturn(opetussuunnitelma());
        when(sisaltoViiteService.getSisaltoViitteet(anyLong(), anyLong(), Mockito.any())).thenReturn(sisaltoviitteet());
    }

    @Test
    public void test_buildNavigation() {
        NavigationNodeDto navigationNodeDto = navigationBuilderDefault.buildNavigation(1l, 1l);

        assertThat(navigationNodeDto).isNotNull();
        assertThat(navigationNodeDto.getChildren()).hasSize(3);
        assertThat(navigationNodeDto.getChildren().get(0).getChildren()).hasSize(2);
        assertThat(navigationNodeDto.getChildren().get(1).getChildren()).hasSize(0);
        assertThat(navigationNodeDto.getChildren().get(2).getChildren()).hasSize(1);

        assertThat(navigationNodeDto.getChildren()).extracting("type").containsExactly(NavigationType.tekstikappale, NavigationType.tekstikappale, NavigationType.tutkinnonosa);
        assertThat(navigationNodeDto.getChildren().get(0).getChildren()).extracting("type").containsExactly(NavigationType.tekstikappale, NavigationType.tekstikappale);
        assertThat(navigationNodeDto.getChildren().get(2).getChildren()).extracting("type").containsExactly(NavigationType.tutkinnonosa);

        assertThat(navigationNodeDto.getChildren().get(2).getLabel().get(Kieli.FI)).isEqualTo("tutkinnonosa1");
    }

    private List<Object> sisaltoviitteet() {
        return Arrays.asList(
                SisaltoViiteKevytDto.builder()
                        .id(0l)
                        .lapset(Arrays.asList(
                                Reference.of(1l),
                                Reference.of(2l),
                                Reference.of(3l)))
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(1l)
                        .tyyppi(SisaltoTyyppi.TEKSTIKAPPALE)
                        .vanhempi(Reference.of(0l))
                        .lapset(Arrays.asList(
                                Reference.of(11l),
                                Reference.of(12l)))
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(2l)
                        .tyyppi(SisaltoTyyppi.TEKSTIKAPPALE)
                        .vanhempi(Reference.of(0l))
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tekstikappale2")).build())
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(11l)
                        .vanhempi(Reference.of(1l))
                        .tyyppi(SisaltoTyyppi.TEKSTIKAPPALE)
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tekstikappale11")).build())
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(12l)
                        .vanhempi(Reference.of(1l))
                        .tyyppi(SisaltoTyyppi.TEKSTIKAPPALE)
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tekstikappale22")).build())
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(3l)
                        .tyyppi(SisaltoTyyppi.TUTKINNONOSA)
                        .vanhempi(Reference.of(0l))
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tutkinnonosa1")).build())
                        .lapset(Arrays.asList(
                                Reference.of(33l)))
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(33l)
                        .tyyppi(SisaltoTyyppi.TUTKINNONOSA)
                        .vanhempi(Reference.of(3l))
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tutkinnonosa33")).build())
                        .build()
        );
    }

    private List<Object> sisaltoviitteetTutkinnonOsille() {
        return Arrays.asList(
                SisaltoViiteKevytDto.builder()
                        .id(0l)
                        .lapset(Collections.singletonList(
                                Reference.of(1l)))
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(1l)
                        .tyyppi(SisaltoTyyppi.TUTKINNONOSAT)
                        .vanhempi(Reference.of(0l))
                        .lapset(Arrays.asList(
                                Reference.of(11l),
                                Reference.of(12l),
                                Reference.of(13l)))
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(11l)
                        .tyyppi(SisaltoTyyppi.TUTKINNONOSA)
                        .vanhempi(Reference.of(1l))
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tutkinnonosa1")).build())
                        .tosa(TutkinnonOsaKevytDto.builder().tyyppi(TutkinnonosaTyyppi.OMA).build())
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(12l)
                        .tyyppi(SisaltoTyyppi.TUTKINNONOSA)
                        .vanhempi(Reference.of(1l))
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tutkinnonosa2")).build())
                        .tosa(TutkinnonOsaKevytDto.builder().tyyppi(TutkinnonosaTyyppi.PERUSTEESTA).build())
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(13l)
                        .tyyppi(SisaltoTyyppi.TEKSTIKAPPALE)
                        .vanhempi(Reference.of(1l))
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tekstikappale")).build())
                        .build(),
                SisaltoViiteKevytDto.builder()
                        .id(14l)
                        .tyyppi(SisaltoTyyppi.LINKKI)
                        .vanhempi(Reference.of(1l))
                        .tekstiKappale(TekstiKappaleKevytDto.builder().nimi(LokalisoituTekstiDto.of("tutkinnonosa3")).build())
                        .tosa(TutkinnonOsaKevytDto.builder().tyyppi(TutkinnonosaTyyppi.OMA).build())
                        .build()
        );
    }

    @Test
    public void test_tutkinnonOsaPilkkominen() {
        when(sisaltoViiteService.getSisaltoViitteet(anyLong(), anyLong(), Mockito.any())).thenReturn(sisaltoviitteetTutkinnonOsille());

        NavigationNodeDto navigationNodeDto = navigationBuilderDefault.buildNavigation(1l, 1l);
        assertThat(navigationNodeDto.getChildren()).hasSize(1);
        assertThat(navigationNodeDto.getChildren().get(0).getChildren())
                .extracting(NavigationNodeDto::getType)
                .containsSequence(
                        NavigationType.tutkinnonosat_pakolliset,
                        NavigationType.tutkinnonosat_paikalliset,
                        NavigationType.tekstikappale);
        List<NavigationNodeDto> tosat = navigationNodeDto.getChildren().get(0).getChildren();
        assertThat(tosat.get(0).getChildren()).extracting(NavigationNodeDto::getId)
                .containsSequence(12L);
        assertThat(tosat.get(1).getChildren()).extracting(NavigationNodeDto::getId)
                .containsSequence(11L);

    }

    private Opetussuunnitelma opetussuunnitelma() {
        Opetussuunnitelma ops = new Opetussuunnitelma();
        ops.setId(1l);
        ops.changeKoulutustoimija(new Koulutustoimija());
        ops.getKoulutustoimija().setId(2l);

        return ops;
    }
}

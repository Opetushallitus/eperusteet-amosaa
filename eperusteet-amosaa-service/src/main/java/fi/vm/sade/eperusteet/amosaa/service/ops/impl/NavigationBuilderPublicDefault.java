package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueKokonaanDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.NavigationBuilderPublic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
@Transactional
public class NavigationBuilderPublicDefault implements NavigationBuilderPublic {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private DtoMapper mapper;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }

    @Override
    public NavigationNodeDto buildNavigation(Long ktId, Long opsId, boolean esikatselu) {
        OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikkiDto = opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(ktId, opsId, esikatselu);
        PerusteKaikkiDto perusteKaikkiDto = opetussuunnitelmaKaikkiDto.getPeruste() != null ? eperusteetService.getPerusteKaikki(opetussuunnitelmaKaikkiDto.getPeruste().getId()) : null;
        SisaltoViiteExportDto root = opetussuunnitelmaKaikkiDto.getSisalto();

        return NavigationNodeDto.of(NavigationType.root, root.getId())
                .addAll(sisaltoviitteet(root, opetussuunnitelmaKaikkiDto, perusteKaikkiDto));
    }

    private List<NavigationNodeDto> sisaltoviitteet(SisaltoViiteExportDto root, OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikkiDto, PerusteKaikkiDto perusteKaikkiDto) {
        return root.getLapset().stream()
                .map(sisaltoViite -> sisaltoviiteToNavigationNode(sisaltoViite, opetussuunnitelmaKaikkiDto, perusteKaikkiDto))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto sisaltoviiteToNavigationNode(SisaltoViiteExportDto sisaltoviite, OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikkiDto, PerusteKaikkiDto perusteKaikkiDto) {
        NavigationNodeDto result = NavigationNodeDto.of(
                        NavigationType.of(sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT)
                                && opetussuunnitelmaKaikkiDto.getKoulutustyyppi() != null
                                && opetussuunnitelmaKaikkiDto.getKoulutustyyppi().isValmaTelma() ?
                                NavigationType.valmatelmaKoulutuksenosat.toString() : sisaltoviite.getTyyppi().toString()),
                        getSisaltoviiteNimi(sisaltoviite),
                        sisaltoviite.getId())
                .meta("koodi", getSisaltoviiteMetaKoodi(sisaltoviite));
        NavigationNodeMetaUtil.asetaMetaTiedot(result, mapper.map(sisaltoviite, SisaltoViiteKevytDto.class));

        if(!CollectionUtils.isEmpty(sisaltoviite.getOsaAlueet())) {
            if (sisaltoviite.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN))) {
                result.add(NavigationNodeDto
                        .of(NavigationType.pakolliset_osaalueet).meta("navigation-subtype", true)
                        .addAll(sisaltoviite.getOsaAlueet().stream()
                                .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN))
                                .map(osaalue -> NavigationNodeDto
                                        .of(NavigationType.osaalue, osaAlueNimi(osaalue, perusteKaikkiDto), osaalue.getId())
                                        .meta("koodi", osaalue.getKoodiArvo())))
                );
            }

            if (sisaltoviite.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN))) {
                result.add(NavigationNodeDto.of(NavigationType.valinnaiset_osaalueet).meta("navigation-subtype", true)
                        .addAll(sisaltoviite.getOsaAlueet().stream()
                                .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN))
                                .map(osaalue -> NavigationNodeDto
                                        .of(NavigationType.osaalue, osaAlueNimi(osaalue, perusteKaikkiDto), osaalue.getId())
                                        .meta("koodi", osaalue.getKoodiArvo())))
                );
            }

            if (sisaltoviite.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))) {
                result.add(NavigationNodeDto.of(NavigationType.paikalliset_osaalueet).meta("navigation-subtype", true)
                        .addAll(sisaltoviite.getOsaAlueet().stream()
                                .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))
                                .map(osaalue -> NavigationNodeDto.of(NavigationType.osaalue, osaalue.getNimi(), osaalue.getId())))
                );
            }
        }

        return result.addAll(sisaltoviite.getLapset() != null ?
                        sisaltoviite.getLapset().stream()
                                .map(lapsi -> sisaltoviiteToNavigationNode(lapsi, opetussuunnitelmaKaikkiDto, perusteKaikkiDto))
                                .collect(Collectors.toList())
                        : Collections.emptyList());
    }

    protected LokalisoituTekstiDto getSisaltoviiteNimi(SisaltoViiteExportDto sisaltoviite) {
        return sisaltoviite.getNimi();
    }

    private LokalisoituTekstiDto osaAlueNimi(OmaOsaAlueDto osaAlue, PerusteKaikkiDto perusteKaikkiDto) {
        for(TutkinnonosaKaikkiDto tutkinnonosa : perusteKaikkiDto.getTutkinnonOsat()) {
            for(OsaAlueKokonaanDto pOsaAlue: tutkinnonosa.getOsaAlueet()) {
                if (pOsaAlue.getId().equals(osaAlue.getPerusteenOsaAlueId())) {
                    return new LokalisoituTekstiDto(pOsaAlue.getNimi().asMap());
                }
            }
        }
        return osaAlue.getNimi();
    }
}

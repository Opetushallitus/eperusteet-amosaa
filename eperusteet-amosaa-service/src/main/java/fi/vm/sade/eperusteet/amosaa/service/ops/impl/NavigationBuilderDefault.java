package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
@Transactional
public class NavigationBuilderDefault implements NavigationBuilder {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }

    @Override
    public NavigationNodeDto buildNavigation(Long ktId, Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        List<SisaltoViiteKevytDto> sisaltoViitteet = sisaltoViiteService.getSisaltoViitteet(ops.getKoulutustoimija().getId(), ops.getId(), SisaltoViiteKevytDto.class);
        SisaltoViiteKevytDto root = sisaltoViitteet.stream()
                .filter(sisaltoViitte -> sisaltoViitte.getVanhempi() == null)
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("sisaltoviitehaku-virhe"));

        return NavigationNodeDto.of(NavigationType.root, root.getId())
                .addAll(sisaltoviitteet(sisaltoViitteet));
    }

    private List<NavigationNodeDto> sisaltoviitteet(List<SisaltoViiteKevytDto> sisaltoViitteet) {
        Map<Long, SisaltoViiteKevytDto> sisaltoViitteetIdMap = sisaltoViitteet.stream().collect(Collectors.toMap(SisaltoViiteKevytDto::getId, Function.identity()));

        return sisaltoViitteet.stream()
                .filter(sisaltoViitte -> sisaltoViitte.getVanhempi() == null)
                .filter(sisaltoviite -> sisaltoviite.getLapset() != null)
                .flatMap(sisaltoviite -> sisaltoviite.getLapset().stream())
                .sorted(Comparator.comparing(Reference::getIdLong, Comparator.comparing(l -> sisaltoViitteetIdMap.get(l).getNavikaatioJarjestys())))
                .map(sisaltoViite -> sisaltoviiteToNavigationNode(sisaltoViitteetIdMap.get(sisaltoViite.getIdLong()), sisaltoViitteetIdMap))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto sisaltoviiteToNavigationNode(SisaltoViiteKevytDto sisaltoviite, Map<Long, SisaltoViiteKevytDto> sisaltoViitteetIdMap) {
        NavigationNodeDto result = NavigationNodeDto.of(
                        NavigationType.of(sisaltoviite.getTyyppi().toString()),
                        sisaltoviite.getNimi(),
                        sisaltoviite.getId())
                    .meta("koodi", getSisaltoviiteMetaKoodi(sisaltoviite));

        if (sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT)) {
            return tutkinnonosatNavigationNode(sisaltoviite, sisaltoViitteetIdMap, result);
        }

        return result.addAll(sisaltoviite.getLapset() != null ? sisaltoviite.getLapset().stream()
                    .map(lapsi -> sisaltoviiteToNavigationNode(sisaltoViitteetIdMap.get(lapsi.getIdLong()), sisaltoViitteetIdMap))
                    .collect(Collectors.toList())
                    : Collections.emptyList());
    }

    private NavigationNodeDto tutkinnonosatNavigationNode(SisaltoViiteKevytDto sisaltoviite, Map<Long, SisaltoViiteKevytDto> sisaltoViitteetIdMap, NavigationNodeDto result) {
        List<NavigationNodeDto> pakolliset = new ArrayList<>();
        List<NavigationNodeDto> paikalliset = new ArrayList<>();
        List<NavigationNodeDto> tuodut = new ArrayList<>();
        List<NavigationNodeDto> muut = new ArrayList<>();

        sisaltoviite.getLapset().forEach(viite -> {
            SisaltoViiteKevytDto lapsi = sisaltoViitteetIdMap.get(viite.getIdLong());
            NavigationNodeDto node = sisaltoviiteToNavigationNode(lapsi, sisaltoViitteetIdMap);

            if (SisaltoTyyppi.TUTKINNONOSA.equals(lapsi.getTyyppi())) {
                if (lapsi.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.PERUSTEESTA)
                    || lapsi.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.YHTEINEN)
                    || lapsi.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.VIERAS)) {
                    pakolliset.add(node);
                }
                else if (lapsi.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.OMA)) {
                    paikalliset.add(node);
                }
                else {
                    muut.add(node);
                }
            }
            else if (SisaltoTyyppi.LINKKI.equals(lapsi.getTyyppi())) {
                tuodut.add(node);
            }
            else {
                muut.add(node);
            }

            if(!CollectionUtils.isEmpty(lapsi.getOsaAlueet())) {
                if (lapsi.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN))) {
                    node.add(NavigationNodeDto.of(NavigationType.pakolliset_osaalueet));
                    node.addAll(lapsi.getOsaAlueet().stream()
                                    .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN))
                                    .map(osaalue -> NavigationNodeDto
                                            .of(NavigationType.osaalue, osaalue.getNimi(), osaalue.getId())
                                            .meta("koodi", osaalue.getKoodiArvo())));
                }

                if (lapsi.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN))) {
                    node.add(NavigationNodeDto.of(NavigationType.valinnaiset_osaalueet));
                    node.addAll(lapsi.getOsaAlueet().stream()
                                    .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN))
                                    .map(osaalue -> NavigationNodeDto
                                            .of(NavigationType.osaalue, osaalue.getNimi(), osaalue.getId())
                                            .meta("koodi", osaalue.getKoodiArvo())));
                }

                if (lapsi.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))) {
                    node.add(NavigationNodeDto.of(NavigationType.paikalliset_osaalueet));
                    node.addAll(lapsi.getOsaAlueet().stream()
                                    .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))
                                    .map(osaalue -> NavigationNodeDto.of(NavigationType.osaalue, osaalue.getNimi(), osaalue.getId())));
                }
            }
        });


        if (!pakolliset.isEmpty()) {
            result.addAll(pakolliset);
        }
        if (!paikalliset.isEmpty()) {
            result.add(NavigationNodeDto.of(NavigationType.tutkinnonosat_paikalliset).addAll(paikalliset));
        }
        if (!tuodut.isEmpty()) {
            result.add(NavigationNodeDto.of(NavigationType.tutkinnonosat_tuodut).addAll(tuodut));
        }
        return result.addAll(muut);
    }
}

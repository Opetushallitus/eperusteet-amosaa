package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Transactional
public class NavigationBuilderDefault implements NavigationBuilder {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private EperusteetService eperusteetService;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }

    @Override
    public NavigationNodeDto buildNavigation(Long ktId, Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        List<SisaltoViiteKevytDto> sisaltoViitteet = sisaltoViiteService.getSisaltoViitteet(ops.getKoulutustoimija().getId(), ops.getId(), SisaltoViiteKevytDto.class);
        PerusteKaikkiDto perusteKaikkiDto = ops.getPeruste() != null ? eperusteetService.getPerusteKaikki(ops.getPeruste().getId()) : null;
        SisaltoViiteKevytDto root = sisaltoViitteet.stream()
                .filter(sisaltoViitte -> sisaltoViitte.getVanhempi() == null)
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("sisaltoviitehaku-virhe"));

        return NavigationNodeDto.of(NavigationType.root, root.getId())
                .addAll(sisaltoviitteet(sisaltoViitteet, perusteKaikkiDto));
    }

    private List<NavigationNodeDto> sisaltoviitteet(List<SisaltoViiteKevytDto> sisaltoViitteet, PerusteKaikkiDto perusteKaikkiDto) {
        Map<Long, SisaltoViiteKevytDto> sisaltoViitteetIdMap = sisaltoViitteet.stream().collect(Collectors.toMap(SisaltoViiteKevytDto::getId, Function.identity()));

        return sisaltoViitteet.stream()
                .filter(sisaltoViitte -> sisaltoViitte.getVanhempi() == null)
                .filter(sisaltoviite -> sisaltoviite.getLapset() != null)
                .flatMap(sisaltoviite -> sisaltoviite.getLapset().stream())
                .filter(sisaltoviite -> sisaltoViitteetIdMap.get(sisaltoviite.getIdLong()) != null)
                .sorted(Comparator.comparing(Reference::getIdLong, Comparator.comparing(l -> sisaltoViitteetIdMap.get(l).getNavikaatioJarjestys())))
                .map(sisaltoViite -> sisaltoviiteToNavigationNode(sisaltoViitteetIdMap.get(sisaltoViite.getIdLong()), sisaltoViitteetIdMap, perusteKaikkiDto))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto sisaltoviiteToNavigationNode(SisaltoViiteKevytDto sisaltoviite, Map<Long, SisaltoViiteKevytDto> sisaltoViitteetIdMap, PerusteKaikkiDto perusteKaikkiDto) {
        NavigationNodeDto result = NavigationNodeDto.of(
                        NavigationType.of(sisaltoviite.getTyyppi().toString()),
                        sisaltoviite.getNimi(),
                        sisaltoviite.getId())
                    .meta("koodi", getSisaltoviiteMetaKoodi(sisaltoviite));
        NavigationNodeMetaUtil.asetaMetaTiedot(result, sisaltoviite, perusteKaikkiDto);
        NavigationNodeMetaUtil.lisaaTutkinnonOsanOsaAlueet(perusteKaikkiDto, sisaltoviite, result, (osaalue) -> true);

        return result.addAll(sisaltoviite.getLapset() != null ? sisaltoviite.getLapset().stream()
                    .filter(lapsi -> sisaltoViitteetIdMap.get(lapsi.getIdLong()) != null)
                    .map(lapsi -> sisaltoviiteToNavigationNode(sisaltoViitteetIdMap.get(lapsi.getIdLong()), sisaltoViitteetIdMap, perusteKaikkiDto))
                    .collect(Collectors.toList())
                    : Collections.emptyList());
    }
}

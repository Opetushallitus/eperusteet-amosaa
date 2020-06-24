package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
                .add(tiedot(opsId))
                .addAll(sisaltoviitteet(sisaltoViitteet));
    }

    private NavigationNodeDto tiedot(Long opsId) {
        return NavigationNodeDto.of(NavigationType.tiedot, null, opsId);
    }

    private List<NavigationNodeDto> sisaltoviitteet(List<SisaltoViiteKevytDto> sisaltoViitteet) {
        Map<Long, SisaltoViiteKevytDto> sisaltoViitteetIdMap = sisaltoViitteet.stream().collect(Collectors.toMap(SisaltoViiteKevytDto::getId, Function.identity()));

        return sisaltoViitteet.stream()
                .filter(sisaltoViitte -> sisaltoViitte.getVanhempi() == null)
                .filter(sisaltoviite -> sisaltoviite.getLapset() != null)
                .map(sisaltoviite -> sisaltoviite.getLapset().stream())
                .flatMap(lapsiRef -> lapsiRef)
                .map(sisaltoViite -> sisaltoviiteToNavigationNode(sisaltoViitteetIdMap.get(sisaltoViite.getIdLong()), sisaltoViitteetIdMap))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto sisaltoviiteToNavigationNode(SisaltoViiteKevytDto sisaltoviite, Map<Long, SisaltoViiteKevytDto> sisaltoViitteetIdMap) {
        return NavigationNodeDto.of(
                NavigationType.of(sisaltoviite.getTyyppi().toString()),
                sisaltoviite.getTekstiKappale() != null ? sisaltoviite.getTekstiKappale().getNimi() : null,
                sisaltoviite.getId())
                .addAll(sisaltoviite.getLapset() != null ? sisaltoviite.getLapset().stream()
                        .map(lapsi -> sisaltoviiteToNavigationNode(sisaltoViitteetIdMap.get(lapsi.getIdLong()), sisaltoViitteetIdMap))
                        .collect(Collectors.toList())
                        : Collections.emptyList());
    }
}

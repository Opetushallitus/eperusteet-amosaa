package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.NavigationBuilderPublic;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderPublicDefault implements NavigationBuilderPublic {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Collections.emptySet();
    }

    @Override
    public NavigationNodeDto buildNavigation(Long ktId, Long opsId) {
        OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikkiDto = opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(ktId, opsId);
        SisaltoViiteExportDto root = opetussuunnitelmaKaikkiDto.getSisalto();

        return NavigationNodeDto.of(NavigationType.root, root.getId())
                .addAll(sisaltoviitteet(root));
    }

    private List<NavigationNodeDto> sisaltoviitteet(SisaltoViiteExportDto root) {
        return root.getLapset().stream()
                .map(sisaltoViite -> sisaltoviiteToNavigationNode(sisaltoViite))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto sisaltoviiteToNavigationNode(SisaltoViiteExportDto sisaltoviite) {
        return NavigationNodeDto.of(
                NavigationType.of(sisaltoviite.getTyyppi().toString()),
                sisaltoviite.getNimi(),
                sisaltoviite.getId())
                .addAll(sisaltoviite.getLapset() != null ?
                        sisaltoviite.getLapset().stream()
                                .map(this::sisaltoviiteToNavigationNode)
                                .collect(Collectors.toList())
                        : Collections.emptyList());
    }
}

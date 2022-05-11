package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
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
                .addAll(sisaltoviitteet(root, opetussuunnitelmaKaikkiDto));
    }

    private List<NavigationNodeDto> sisaltoviitteet(SisaltoViiteExportDto root, OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikkiDto) {
        return root.getLapset().stream()
                .map(sisaltoViite -> sisaltoviiteToNavigationNode(sisaltoViite, opetussuunnitelmaKaikkiDto))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto sisaltoviiteToNavigationNode(SisaltoViiteExportDto sisaltoviite, OpetussuunnitelmaKaikkiDto opetussuunnitelmaKaikkiDto) {
        return NavigationNodeDto.of(
                NavigationType.of(sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSAT)
                        && opetussuunnitelmaKaikkiDto.getKoulutustyyppi() != null
                        && opetussuunnitelmaKaikkiDto.getKoulutustyyppi().isValmaTelma() ?
                        NavigationType.valmatelmaKoulutuksenosat.toString() : sisaltoviite.getTyyppi().toString()),
                getSisaltoviiteNimi(sisaltoviite),
                sisaltoviite.getId())
                .addAll(sisaltoviite.getLapset() != null ?
                        sisaltoviite.getLapset().stream()
                                .map(lapsi -> sisaltoviiteToNavigationNode(lapsi, opetussuunnitelmaKaikkiDto))
                                .collect(Collectors.toList())
                        : Collections.emptyList());
    }

    protected LokalisoituTekstiDto getSisaltoviiteNimi(SisaltoViiteExportDto sisaltoviite) {
        return sisaltoviite.getNimi();
    }
}

package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
public class NavigationBuilderTuvaPublic extends NavigationBuilderPublicDefault {

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long ktId, Long opsId, boolean esikatselu) {
        return NavigationBuilderUtils.reOrderTuvaKoulutuksenOsat(super.buildNavigation(ktId, opsId, esikatselu));
    }

    @Override
    public String getSisaltoviiteMetaKoodi(SisaltoViiteExportDto sisaltoviite) {
        if (sisaltoviite.getKoulutuksenosa() != null) {
            return sisaltoviite.getKoulutuksenosa().getKoodiArvo();

        }
        return null;
    }
}

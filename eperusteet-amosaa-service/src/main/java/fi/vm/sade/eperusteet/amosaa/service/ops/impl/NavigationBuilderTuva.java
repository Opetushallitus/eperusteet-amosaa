package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Transactional
public class NavigationBuilderTuva extends NavigationBuilderDefault {

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.TUTKINTOONVALMENTAVA);
    }

    @Override
    public String getSisaltoviiteMetaKoodi(SisaltoViiteKevytDto sisaltoviite) {
        if (sisaltoviite.getKoulutuksenosa() != null) {
            return sisaltoviite.getKoulutuksenosa().getKoodiArvo();

        }
        return null;
    }
}

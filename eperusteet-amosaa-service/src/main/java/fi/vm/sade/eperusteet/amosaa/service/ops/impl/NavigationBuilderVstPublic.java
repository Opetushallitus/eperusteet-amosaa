package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderVstPublic extends NavigationBuilderPublicDefault {

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.VAPAASIVISTYSTYO, KoulutusTyyppi.VAPAASIVISTYSTYOLUKUTAITO);
    }

    @Override
    protected LokalisoituTekstiDto getSisaltoviiteNimi(SisaltoViiteExportDto sisaltoviite) {
        if (sisaltoviite.getOpintokokonaisuus() != null && sisaltoviite.getOpintokokonaisuus().getKoodiArvo() != null) {
            return new LokalisoituTekstiDto(sisaltoviite.getNimi().getTekstit().keySet()
                    .stream()
                    .collect(Collectors.toMap(Kieli::toString, kieli -> sisaltoviite.getNimi().getTekstit().get(kieli) + " (" + sisaltoviite.getOpintokokonaisuus().getKoodiArvo() + ")")));
        }

        return sisaltoviite.getNimi();
    }
}

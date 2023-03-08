package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NavigationNodeMetaUtil {

    public static void asetaMetaTiedot(NavigationNodeDto navigationNodeDto, SisaltoViiteKevytDto sisaltoviite) {

        if(sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA) && sisaltoviite.getTosa() != null && sisaltoviite.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.OMA)) {
            navigationNodeDto.meta("postfix_label", "tutkinnon-osa-paikallinen-merkki");
            navigationNodeDto.meta("postfix_tooltip", "paikallisesti-luotu-tutkinnon-osa");
        }
    }
}

package fi.vm.sade.eperusteet.amosaa.resource.config;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class InitBinderControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Tyyppi.class, new EnumToUpperCaseEditor<>(Tyyppi.class));
        binder.registerCustomEditor(KoulutusTyyppi.class, new KoulutustyyppiEditor());
        binder.registerCustomEditor(Tila.class, new EnumToUpperCaseEditor<>(Tila.class));
        binder.registerCustomEditor(OpsTyyppi.class, new EnumToUpperCaseEditor<>(OpsTyyppi.class));
    }

}

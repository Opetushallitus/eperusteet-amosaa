package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class KoulutustoimijaIdGetterAbstractController {
    @Lazy
    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    // Käytetään, koska ktId voi oll joko ktId tai organisaatioId
    @ModelAttribute("solvedKtId")
    protected Long getKtId(@PathVariable(value = "ktId", required = false) String ktId) {
        if (ktId != null) {
            return koulutustoimijaService.getKoulutustoimija(ktId);
        }
        else {
            return null;
        }
    }
}

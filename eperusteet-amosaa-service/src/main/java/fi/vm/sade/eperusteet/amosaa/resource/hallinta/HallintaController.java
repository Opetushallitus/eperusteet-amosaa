package fi.vm.sade.eperusteet.amosaa.resource.hallinta;

import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hallinta")
@Hidden
public class HallintaController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(value = "/updateopetussuunnitelmatutkintonimikkeetosaamisalat", method = RequestMethod.GET)
    public void updateOpetussuunnitelmaTutkintonimikkeetOsaamisalat() {
        opetussuunnitelmaService.updateOpetussuunnitelmaSisaltoviitePiilotukset();
    }
}

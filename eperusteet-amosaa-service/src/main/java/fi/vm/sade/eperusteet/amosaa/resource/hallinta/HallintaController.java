package fi.vm.sade.eperusteet.amosaa.resource.hallinta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/hallinta")
@ApiIgnore
public class HallintaController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(value = "/updateopetussuunnitelmatutkintonimikkeetosaamisalat", method = RequestMethod.GET)
    public void updateOpetussuunnitelmaTutkintonimikkeetOsaamisalat() {
        opetussuunnitelmaService.updateOpetussuunnitelmaSisaltoviitePiilotukset();
    }
}

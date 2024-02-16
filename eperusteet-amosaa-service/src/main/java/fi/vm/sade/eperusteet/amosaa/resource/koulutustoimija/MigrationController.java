package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/migrate")
@InternalApi
public class MigrationController {

    @Autowired
    private OpetussuunnitelmaService service;

    @Autowired
    private MaintenanceService maintenanceService;

    @RequestMapping(method = RequestMethod.GET)
    public void migrate() {
        service.mapKoulutustyyppi();
    }

}

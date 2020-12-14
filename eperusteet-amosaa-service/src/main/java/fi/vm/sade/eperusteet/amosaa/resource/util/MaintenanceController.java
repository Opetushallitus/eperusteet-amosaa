package fi.vm.sade.eperusteet.amosaa.resource.util;

import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@InternalApi
@RestController
@RequestMapping(value = "/maintenance")
@Profile("!test")
@Api("Maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @RequestMapping(value = "/organisaatiotyypit/paivita", method = GET)
    public void teeJulkaisut() {
        maintenanceService.updateKoulutustoimijaTyypit();
    }
}

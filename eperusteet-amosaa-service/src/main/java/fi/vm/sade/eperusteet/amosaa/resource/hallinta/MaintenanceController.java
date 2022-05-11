package fi.vm.sade.eperusteet.amosaa.resource.hallinta;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@InternalApi
@RestController
@RequestMapping(value = "/maintenance")
@Profile("!test")
@Api("Maintenance")
public class MaintenanceController {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private MaintenanceService maintenanceService;

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceController.class);

    @RequestMapping(value = "/cacheclear/{cache}", method = GET)
    public ResponseEntity clearCache(@PathVariable final String cache) {
        cacheManager.getCache(cache).clear();
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @RequestMapping(value = "/julkaisut", method = GET)
    public void teeJulkaisut(
            @RequestParam(value = "julkaisekaikki", required = false, defaultValue = "false") boolean julkaiseKaikki,
            @RequestParam(value = "koulutustyypit", required = false) final Set<String> koulutustyypit,
            @RequestParam(value = "opstyyppi", required = false, defaultValue = "ops") final String opstyyppi
    ) {
        logger.info("kutsuttu teeJulkaisut endpointtia");
        maintenanceService.teeJulkaisut(
                julkaiseKaikki,
                koulutustyypit != null ? koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()) : null,
                OpsTyyppi.of(opstyyppi));
    }

}

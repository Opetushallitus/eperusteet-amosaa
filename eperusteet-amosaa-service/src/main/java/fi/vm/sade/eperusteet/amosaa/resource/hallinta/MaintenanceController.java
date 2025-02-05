package fi.vm.sade.eperusteet.amosaa.resource.hallinta;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/api/maintenance")
@Profile("!test")
@Tag(name = "Maintenance")
@Slf4j
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @RequestMapping(value = "/cacheclear/{cache}", method = GET)
    public ResponseEntity clearCache(@PathVariable final String cache) {
        maintenanceService.clearCache(cache);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @RequestMapping(value = "/julkaisut", method = GET)
    public void teeJulkaisut(
            @RequestParam(value = "julkaisekaikki", required = false, defaultValue = "false") boolean julkaiseKaikki,
            @RequestParam(value = "koulutustyypit", required = false) final Set<String> koulutustyypit,
            @RequestParam(value = "opstyyppi", required = false, defaultValue = "ops") final String opstyyppi
    ) {
        log.info("kutsuttu teeJulkaisut endpointtia");
        maintenanceService.teeJulkaisut(
                julkaiseKaikki,
                koulutustyypit != null ? koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()) : null,
                OpsTyyppi.of(opstyyppi));
    }

    @RequestMapping(value = "/poistajulkaisut/{opsId}", method = GET)
    public ResponseEntity poistaJulkaisut(@PathVariable final Long opsId) {
        maintenanceService.poistaJulkaisut(opsId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/paivita/koulutustoimija/oppilaitostyyppi", method = GET)
    public ResponseEntity paivitaKoulutustoimijaOppilaitostyypit() {
        maintenanceService.paivitaKoulutustoimijaOppilaitostyypi();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

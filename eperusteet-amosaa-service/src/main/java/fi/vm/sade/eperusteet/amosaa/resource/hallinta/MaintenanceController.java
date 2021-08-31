package fi.vm.sade.eperusteet.amosaa.resource.hallinta;


import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    @RequestMapping(value = "/cacheclear/{cache}", method = GET)
    public ResponseEntity clearCache(@PathVariable final String cache) {
        cacheManager.getCache(cache).clear();
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @RequestMapping(value = "/julkaisut", method = GET)
    public void teeJulkaisut(
            @RequestParam(value = "julkaisekaikki", defaultValue = "false") boolean julkaiseKaikki,
            @RequestParam(value = "koulutustyypit") final Set<String> koulutustyypit
    ) {
        maintenanceService.teeJulkaisut(julkaiseKaikki, koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()));
    }

}

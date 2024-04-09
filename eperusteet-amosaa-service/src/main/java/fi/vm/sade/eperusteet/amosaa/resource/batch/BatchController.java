package fi.vm.sade.eperusteet.amosaa.resource.batch;

import fi.vm.sade.eperusteet.amosaa.domain.batch.BatchStepExecution;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.repository.batch.BatchStepExecutionRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceJulkaisuTarkistus;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import io.swagger.annotations.Api;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/batch")
@InternalApi
@Api("batch")
public class BatchController {

    @Autowired
    private BatchStepExecutionRepository batchStepExecutionRepository;

    @Autowired
    private MaintenanceService maintenanceService;

    @RequestMapping(value = "", method = GET)
    @PreAuthorize("hasPermission(null, 'oph', 'HALLINTA')")
    public ResponseEntity<List<BatchStepExecution>> getBatchStepExecution() {
        return new ResponseEntity(batchStepExecutionRepository.findAllByOrderByStepExecutionIdDesc(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{job}", method = GET)
    public ResponseEntity<String> kaynnistaJob(
            @PathVariable final String job,
            @RequestParam(value = "julkaisutarkistus", required = false, defaultValue = "JULKAISTUT") String julkaisutarkistus,
            @RequestParam(value = "koulutustyypit", required = false) final Set<String> koulutustyypit,
            @RequestParam(value = "opstyyppi", required = false, defaultValue = "ops") final String opsTyyppi) throws Exception {

        Map<String, String> parametrit = new HashMap<String, String>();

        if ("julkaisuJob".equals(job)) {
            parametrit.put("julkaisutarkistus", julkaisutarkistus);
            parametrit.put("opsTyyppi", opsTyyppi);
            if (CollectionUtils.isNotEmpty(koulutustyypit)) {
                parametrit.put("koulutustyypit", String.join(",", koulutustyypit));
            }
        }

        maintenanceService.kaynnistaJob(job, parametrit);
        return ResponseEntity.status(HttpStatus.OK).body("Eräajo käynnistetty");
    }
}

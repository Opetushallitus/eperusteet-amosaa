package fi.vm.sade.eperusteet.amosaa.resource.dokumentti;

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.dokumentti.util.DokumenttiTyyppi;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/koulutustoimijat/{baseId}/ops/{id}/dokumentti")
@Api(value = "dokumentit")
@InternalApi
public class OpsDokumenttiController implements DokumenttiAbstractController {

    @Autowired
    DokumenttiService service;


    @Override
    public DokumenttiService service() {
        return service;
    }

    @Override
    public DokumenttiTyyppi tyyppi() {
        return DokumenttiTyyppi.OPS;
    }
}

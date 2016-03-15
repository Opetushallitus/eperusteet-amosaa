package fi.vm.sade.eperusteet.amosaa.resource.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTyyppi;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/koulutustoimijat/{baseId}/yhteiset/{id}/dokumentti")
@Api(value = "dokumentit")
public class YhteisetDokumenttiController implements DokumenttiAbstractController {

    @Autowired
    DokumenttiService service;


    @Override
    public DokumenttiService service() {
        return service;
    }

    @Override
    public DokumenttiTyyppi tyyppi() {
        return DokumenttiTyyppi.YHTEISET;
    }
}

package fi.vm.sade.eperusteet.amosaa.resource.dokumentti;

import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti")
@Api(value = "dokumentit")
public class DokumenttiController implements DokumenttiAbstractController {

    @Autowired
    DokumenttiService service;

    @Autowired
    DokumenttiRepository repository;

    @Override
    public DokumenttiRepository repository() {
        return repository;
    }

    @Override
    public DokumenttiService service() {
        return service;
    }

}

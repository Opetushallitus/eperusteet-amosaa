package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author isaul
 */

@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tutkinnonosat")
@ApiIgnore
public class TutkinnonOsaController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private TutkinnonosaRepository tutkinnonosaRepository;

    @Autowired
    private DtoMapper mapper;

    // todo: Rajapinta muita varten
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/viite/{id}", method = GET)
    public ResponseEntity<Tutkinnonosa> get(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        Tutkinnonosa tutkinnonosa = tutkinnonosaRepository.findOne(id);
        return ResponseEntity.ok(mapper.map(tutkinnonosa, Tutkinnonosa.class));
    }
}

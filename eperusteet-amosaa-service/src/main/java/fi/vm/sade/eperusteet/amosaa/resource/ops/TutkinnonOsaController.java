package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.dto.OletusToteutusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TutkinnonosaDto;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author isaul
 */

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tutkinnonosat")
@InternalApi
@Api(value = "Tutkinnonosa")
public class TutkinnonOsaController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private TutkinnonosaRepository tutkinnonosaRepository;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private DtoMapper mapper;

    // todo: Rajapinta muita varten
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/viite/{id}", method = GET)
    public ResponseEntity<Tutkinnonosa> haeTutkinnonosa(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        Tutkinnonosa tutkinnonosa = tutkinnonosaRepository.findOne(id);
        return ResponseEntity.ok(mapper.map(tutkinnonosa, Tutkinnonosa.class));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/oletustoteutukset", method = GET)
    public ResponseEntity<List<OletusToteutusDto>> haeOletusTutkinnonosaToteutukset(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return ResponseEntity.ok(sisaltoViiteService.tutkinnonosienOletusToteutukset(ktId, opsId));
    }
}

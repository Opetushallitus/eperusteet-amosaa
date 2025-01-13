package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.dto.OletusToteutusDto;
import fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa.TutkinnonosaRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tutkinnonosat")
@InternalApi
@Tag(name = "Tutkinnonosa")
public class TutkinnonOsaController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private TutkinnonosaRepository tutkinnonosaRepository;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private DtoMapper mapper;

    // todo: Rajapinta muita varten
    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/viite/{id}", method = GET)
    public ResponseEntity<Tutkinnonosa> haeTutkinnonosa(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        Tutkinnonosa tutkinnonosa = tutkinnonosaRepository.findOne(id);
        return ResponseEntity.ok(mapper.map(tutkinnonosa, Tutkinnonosa.class));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/oletustoteutukset", method = GET)
    public ResponseEntity<List<OletusToteutusDto>> haeOletusTutkinnonosaToteutukset(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return ResponseEntity.ok(sisaltoViiteService.tutkinnonosienOletusToteutukset(ktId, opsId));
    }
}

package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.OletusToteutusDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.ops.OsaAlueService;
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
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/osaalue")
@InternalApi
@Tag(name = "OsaAlue")
public class OsaAlueController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private OsaAlueService osaAlueService;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/oletustoteutukset", method = GET)
    public ResponseEntity<List<OletusToteutusDto>> haeOletusOsaAlueToteutukset(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return ResponseEntity.ok(osaAlueService.osaAlueidenOletusToteutukset(ktId, opsId));
    }
}

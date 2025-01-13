package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.ops.TermistoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}")
@InternalApi
public class TermistoController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private TermistoService termistoService;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/termisto", method = GET)
    public ResponseEntity<List<TermiDto>> getAll(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return ResponseEntity.ok(termistoService.getTermit(ktId));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/termisto/{termiId}", method = GET)
    public ResponseEntity<TermiDto> get(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long termiId
    ) {
        return ResponseEntity.ok(termistoService.getTermi(ktId, termiId));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/termisto/{avain}/avain", method = GET)
    public ResponseEntity<TermiDto> get(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String avain
    ) {
        return ResponseEntity.ok(termistoService.getTermiByAvain(ktId, avain));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/termisto", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TermiDto> addTermi(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody TermiDto dto
    ) {
        dto.setId(null);
        return ResponseEntity.ok(termistoService.addTermi(ktId, dto));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/termisto/{termiId}", method = PUT)
    public ResponseEntity<TermiDto> updateTermi(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long termiId,
            @RequestBody TermiDto dto
    ) {
        dto.setId(termiId);
        return ResponseEntity.ok(termistoService.updateTermi(ktId, dto));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/termisto/{termiId}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTermi(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long termiId
    ) {
        termistoService.deleteTermi(ktId, termiId);
    }
}

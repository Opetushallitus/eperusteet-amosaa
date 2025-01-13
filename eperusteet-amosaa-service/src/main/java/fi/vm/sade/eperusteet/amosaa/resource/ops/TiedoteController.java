package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tiedotteet", description = "Tiedotteiden hallinta")
@InternalApi
public class TiedoteController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private TiedoteService tiedoteService;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = GET, value = "/tiedotteet")
    public ResponseEntity<List<TiedoteDto>> getAll(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return ResponseEntity.ok(tiedoteService.getTiedotteet(ktId));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tiedotteet/{id}", method = GET)
    public ResponseEntity<TiedoteDto> getTiedote(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id
    ) {
        return ResponseEntity.ok(tiedoteService.getTiedote(ktId, id));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = POST, value = "/tiedotteet")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TiedoteDto> addTiedote(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody TiedoteDto tiedoteDto
    ) {
        return ResponseEntity.ok(tiedoteService.addTiedote(ktId, tiedoteDto));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tiedotteet/{id}", method = PUT)
    public ResponseEntity<TiedoteDto> updateTiedote(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id,
            @RequestBody TiedoteDto tiedoteDto
    ) {
        return ResponseEntity.ok(tiedoteService.updateTiedote(ktId, tiedoteDto));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tiedotteet/{id}/kuittaa", method = POST)
    public void updateTiedote(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id
    ) {
        tiedoteService.kuittaaLuetuksi(ktId, id);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tiedotteet/{id}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTiedote(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id
    ) {
        tiedoteService.deleteTiedote(ktId, id);
    }
}

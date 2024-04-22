package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}")
@Api(value = "Tiedotteet", description = "Tiedotteiden hallinta")
@InternalApi
public class TiedoteController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private TiedoteService tiedoteService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = GET, value = "/tiedotteet")
    public ResponseEntity<List<TiedoteDto>> getAll(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return ResponseEntity.ok(tiedoteService.getTiedotteet(ktId));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tiedotteet/{id}", method = GET)
    public ResponseEntity<TiedoteDto> getTiedote(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id
    ) {
        return ResponseEntity.ok(tiedoteService.getTiedote(ktId, id));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = POST, value = "/tiedotteet")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TiedoteDto> addTiedote(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody TiedoteDto tiedoteDto
    ) {
        return ResponseEntity.ok(tiedoteService.addTiedote(ktId, tiedoteDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tiedotteet/{id}", method = PUT)
    public ResponseEntity<TiedoteDto> updateTiedote(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id,
            @RequestBody TiedoteDto tiedoteDto
    ) {
        return ResponseEntity.ok(tiedoteService.updateTiedote(ktId, tiedoteDto));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tiedotteet/{id}/kuittaa", method = POST)
    public void updateTiedote(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id
    ) {
        tiedoteService.kuittaaLuetuksi(ktId, id);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tiedotteet/{id}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTiedote(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id
    ) {
        tiedoteService.deleteTiedote(ktId, id);
    }
}

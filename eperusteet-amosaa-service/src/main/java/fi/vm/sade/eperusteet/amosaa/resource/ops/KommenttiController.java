package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.KommenttiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.teksti.KommenttiService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{tkvId}/kommentit")
@InternalApi
public class KommenttiController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private KommenttiService kommenttiService;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = GET)
    public ResponseEntity<List<KommenttiDto>> getAll(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId
    ) {
        List<KommenttiDto> t = kommenttiService.getAllByTekstikappaleviite(ktId, opsId, tkvId);
        return new ResponseEntity<>(t, HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = POST)
    public ResponseEntity<KommenttiDto> add(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @RequestBody KommenttiDto body
    ) {
        body.setTekstikappaleviiteId(tkvId);

        return new ResponseEntity<>(kommenttiService.add(ktId, opsId, body), HttpStatus.CREATED);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<KommenttiDto> get(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @PathVariable final long id
    ) {
        KommenttiDto kommenttiDto = kommenttiService.get(ktId, opsId, id);
        return new ResponseEntity<>(kommenttiDto, kommenttiDto == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<KommenttiDto> update(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @PathVariable final long id,
            @RequestBody KommenttiDto body) {
        return new ResponseEntity<>(kommenttiService.update(ktId, opsId, id, body), HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{id}", method = DELETE)
    public void delete(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @PathVariable final long id
    ) {
        kommenttiService.delete(ktId, opsId, id);
    }
}

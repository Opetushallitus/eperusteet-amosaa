package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.KommenttiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.teksti.KommenttiService;
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
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{tkvId}/kommentit")
@InternalApi
public class KommenttiController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private KommenttiService kommenttiService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = GET)
    public ResponseEntity<List<KommenttiDto>> getAll(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId
    ) {
        List<KommenttiDto> t = kommenttiService.getAllByTekstikappaleviite(ktId, opsId, tkvId);
        return new ResponseEntity<>(t, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = POST)
    public ResponseEntity<KommenttiDto> add(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @RequestBody KommenttiDto body
    ) {
        body.setTekstikappaleviiteId(tkvId);

        return new ResponseEntity<>(kommenttiService.add(ktId, opsId, body), HttpStatus.CREATED);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<KommenttiDto> get(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @PathVariable final long id
    ) {
        KommenttiDto kommenttiDto = kommenttiService.get(ktId, opsId, id);
        return new ResponseEntity<>(kommenttiDto, kommenttiDto == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<KommenttiDto> update(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @PathVariable final long id,
            @RequestBody KommenttiDto body) {
        return new ResponseEntity<>(kommenttiService.update(ktId, opsId, id, body), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = DELETE)
    public void delete(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final long opsId,
            @PathVariable final long tkvId,
            @PathVariable final long id
    ) {
        kommenttiService.delete(ktId, opsId, id);
    }
}

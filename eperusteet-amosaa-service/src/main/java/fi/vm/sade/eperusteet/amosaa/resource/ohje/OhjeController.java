package fi.vm.sade.eperusteet.amosaa.resource.ohje;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.dto.ohje.OhjeDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.ohje.OhjeService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/ohjeet")
@InternalApi
@Api("Ohjeet")
public class OhjeController {

    @Autowired
    private OhjeService service;

    @RequestMapping(method = GET)
    public ResponseEntity<List<OhjeDto>> getOhjeet(@RequestParam(value = "toteutus", required = false, defaultValue = "ammatillinen") final String toteutus) {
        return ResponseEntity.ok(service.getOhjeet(KoulutustyyppiToteutus.of(toteutus)));
    }

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OhjeDto> addOhje(
            @RequestBody OhjeDto dto) {
        dto.setId(null);
        return ResponseEntity.ok(service.addOhje(dto));
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<OhjeDto> editOhje(
            @PathVariable Long id,
            @RequestBody OhjeDto dto) {
        return ResponseEntity.ok(service.editOhje(id, dto));
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity removeOhje(
            @PathVariable Long id) {
        service.removeOhje(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

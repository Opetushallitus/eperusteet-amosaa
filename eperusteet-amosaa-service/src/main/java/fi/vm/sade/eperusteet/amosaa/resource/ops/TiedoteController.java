package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

/**
 * Created by richard.vancamp on 29/03/16.
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}")
@InternalApi
public class TiedoteController {

    @Autowired
    private TiedoteService tiedoteService;

    @RequestMapping(value = "/tiedotteet", method = GET)
    public ResponseEntity<List<TiedoteDto>> getAll(
            @PathVariable final Long ktId) {
        return ResponseEntity.ok(tiedoteService.getAll(ktId));
    }

    @RequestMapping(value = "/tiedotteet", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TiedoteDto> addTiedote(
            @PathVariable final Long ktId,
            @RequestBody TiedoteDto tiedoteDto) {
        return ResponseEntity.ok(tiedoteService.addTiedote(ktId, tiedoteDto));
    }

    @RequestMapping(value = "/tiedotteet/{id}", method = PUT)
    public ResponseEntity<TiedoteDto> updateTiedote(
            @PathVariable final Long ktId,
            @PathVariable final Long id,
            @RequestBody TiedoteDto tiedoteDto) {
        return ResponseEntity.ok(tiedoteService.updateTiedote(ktId, tiedoteDto));
    }

    @RequestMapping(value = "/tiedotteet/{id}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTiedote(
            @PathVariable final Long ktId,
            @PathVariable final Long id) {
        tiedoteService.deleteTiedote(ktId, id);
    }

}


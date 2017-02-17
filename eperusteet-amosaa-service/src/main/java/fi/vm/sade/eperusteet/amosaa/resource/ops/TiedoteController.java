package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.KAYTTAJA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.KOULUTUSTOIMIJA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.TIEDOTE_KUITTAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.TIEDOTE_MUOKKAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.TIEDOTE_POISTO;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.ops.TiedoteService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by richard.vancamp on 29/03/16.
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}")
@Api(value = "Tiedotteet", description = "Tiedotteiden hallinta")
public class TiedoteController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private TiedoteService tiedoteService;

    @RequestMapping(method = GET, value = "/tiedotteet")
    public ResponseEntity<List<TiedoteDto>> getAll(
            @ModelAttribute("solvedKtId") final Long ktId) {
        return ResponseEntity.ok(tiedoteService.getTiedotteet(ktId));
    }

    @RequestMapping(value = "/tiedotteet/{id}", method = GET)
    public ResponseEntity<TiedoteDto> getTiedote(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id) {
        return ResponseEntity.ok(tiedoteService.getTiedote(ktId, id));
    }

    @RequestMapping(method = POST, value = "/tiedotteet")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TiedoteDto> addTiedote(
            @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody TiedoteDto tiedoteDto) {
        LogMessage.builder(KOULUTUSTOIMIJA, TIEDOTE_POISTO).log();
        return ResponseEntity.ok(tiedoteService.addTiedote(ktId, tiedoteDto));
    }

    @RequestMapping(value = "/tiedotteet/{id}", method = PUT)
    public ResponseEntity<TiedoteDto> updateTiedote(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id,
            @RequestBody TiedoteDto tiedoteDto) {
        LogMessage.builder(KOULUTUSTOIMIJA, TIEDOTE_MUOKKAUS).log();
        return ResponseEntity.ok(tiedoteService.updateTiedote(ktId, tiedoteDto));
    }

    @RequestMapping(value = "/tiedotteet/{id}/kuittaa", method = POST)
    public void updateTiedote(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id) {
        LogMessage.builder(KAYTTAJA, TIEDOTE_KUITTAUS).log();
        tiedoteService.kuittaaLuetuksi(ktId, id);
    }

    @RequestMapping(value = "/tiedotteet/{id}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTiedote(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long id) {
        LogMessage.builder(KOULUTUSTOIMIJA, TIEDOTE_POISTO).log();
        tiedoteService.deleteTiedote(ktId, id);
    }

}

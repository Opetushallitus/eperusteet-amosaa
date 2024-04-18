package fi.vm.sade.eperusteet.amosaa.resource.locks;

import fi.vm.sade.eperusteet.amosaa.dto.LukkoDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.locks.contexts.SisaltoViiteCtx;
import fi.vm.sade.eperusteet.amosaa.resource.util.Etags;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{svId}/lukko")
@Api(value = "SisaltoviiteLukko")
@InternalApi
public class SisaltoViiteLockController {
    @Autowired
    SisaltoViiteService service;

    @RequestMapping(method = GET)
    public ResponseEntity<LukkoDto> checkLock(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        SisaltoViiteCtx ctx = SisaltoViiteCtx.of(ktId, opsId, svId);
        LukkoDto lock = service.getLock(ctx);
        return lock == null ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(lock, Etags.eTagHeader(lock.getRevisio()), HttpStatus.LOCKED);
    }

    @RequestMapping(value = "/check", method = GET)
    public ResponseEntity<LukkoDto> getLock(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId) {
        SisaltoViiteCtx ctx = SisaltoViiteCtx.of(ktId, opsId, svId);
        LukkoDto lock = service.getLock(ctx);
        return lock == null ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(lock, Etags.eTagHeader(lock.getRevisio()), HttpStatus.OK);
    }

    @RequestMapping(method = POST)
    public ResponseEntity<LukkoDto> lock(@PathVariable final Long ktId,
                                         @PathVariable final Long opsId,
                                         @PathVariable final Long svId,
                                         @RequestHeader(value = "If-Match", required = false) String eTag) {
        SisaltoViiteCtx ctx = SisaltoViiteCtx.of(ktId, opsId, svId);
        LukkoDto lock = service.lock(ctx, Etags.revisionOf(eTag));
        if (lock == null) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        } else {
            return new ResponseEntity<>(lock, Etags.eTagHeader(lock.getRevisio()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlock(@PathVariable final Long ktId,
                       @PathVariable final Long opsId,
                       @PathVariable final Long svId) {
        SisaltoViiteCtx ctx = SisaltoViiteCtx.of(ktId, opsId, svId);
        service.unlock(ctx);
    }

}

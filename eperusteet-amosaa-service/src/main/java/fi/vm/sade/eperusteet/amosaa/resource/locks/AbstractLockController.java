/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.amosaa.resource.locks;

import fi.vm.sade.eperusteet.amosaa.dto.LukkoDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.util.Etags;
import fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaAudit;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.SISALTO_LUKITUS;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.locking.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author jhyoty
 * @param <T>
 */
@InternalApi
public abstract class AbstractLockController<T> {
    @Autowired
    private EperusteetAmosaaAudit audit;

    @RequestMapping(method = GET)
    public ResponseEntity<LukkoDto> checkLock(T ctx) {
        LukkoDto lock = service().getLock(ctx);
        return lock == null ? new ResponseEntity<>(HttpStatus.OK)
            : new ResponseEntity<>(lock, Etags.eTagHeader(lock.getRevisio()), HttpStatus.LOCKED);
    }

    @RequestMapping(method = POST)
    public ResponseEntity<LukkoDto> lock(T ctx,
                                         @RequestHeader(value = "If-Match", required = false) String eTag) {
        LukkoDto lock = service().lock(ctx, Etags.revisionOf(eTag));
        return audit.withAudit(LogMessage.builder(null, null, OPETUSSUUNNITELMA, SISALTO_LUKITUS), (Void) -> {
            if (lock == null) {
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            } else {
                return new ResponseEntity<>(lock, Etags.eTagHeader(lock.getRevisio()), HttpStatus.CREATED);
            }
        });
    }

    @RequestMapping(method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlock(T ctx) {
        service().unlock(ctx);
    }

    protected abstract LockService<T> service();
}

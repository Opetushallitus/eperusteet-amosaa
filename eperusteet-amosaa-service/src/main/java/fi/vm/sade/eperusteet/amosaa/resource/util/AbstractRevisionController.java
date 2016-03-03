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

package fi.vm.sade.eperusteet.amosaa.resource.util;

import com.codahale.metrics.annotation.Timed;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author nkala
 */

public interface AbstractRevisionController {

    @RequestMapping(value = "/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    @Timed
    default ResponseEntity<Revision> getLatestRevision(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(getService().getLatestRevision(baseId, id));
    }

    @RequestMapping(value = "/versiot", method = RequestMethod.GET)
    @InternalApi
    @Timed
    default ResponseEntity<List<Revision>> getRevisions(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id) {
        return ResponseEntity.ok(getService().getRevisions(baseId, id));
    }

    @RequestMapping(value = "/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    @Timed
    default ResponseEntity<Object> getRevisions(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id,
            @PathVariable("revId") final Integer revId) {
        return ResponseEntity.ok(getService().getData(baseId, id, revId));
    }

    RevisionService getService();
}

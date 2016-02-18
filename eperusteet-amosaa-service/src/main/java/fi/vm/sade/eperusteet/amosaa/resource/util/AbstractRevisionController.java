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

import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author nkala
 */
public interface AbstractRevisionController {

    @RequestMapping(value = "/versiot/uusin", method = RequestMethod.GET)
    @ResponseBody
    @InternalApi
    default public Revision getLatestRevision(
            @PathVariable("kid") final Long kid,
            @PathVariable("id") final Long id) {
        return getService().getLatestRevision(kid, id);
    }

    @RequestMapping(value = "/versiot", method = RequestMethod.GET)
    @ResponseBody
    @InternalApi
    default public List<Revision> getRevisions(
            @PathVariable("kid") final Long kid,
            @PathVariable("id") final Long id) {
        return getService().getRevisions(kid, id);
    }

    RevisionService getService();
}

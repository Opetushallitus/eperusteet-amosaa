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

package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author nkala
 */
@Component
public class KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private KoulutustoimijaService ktService;

    // Käytetään, koska ktId voi oll joko ktId tai organisaatioId
    @ModelAttribute("solvedKtId")
    protected Long getKtId(@PathVariable(value = "ktId", required = false) String ktId) {
        if (ktId != null) {
            return ktService.getKoulutustoimija(ktId);
        }
        else {
            return null;
        }
    }
}

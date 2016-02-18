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

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/kayttajaoikeudet")
@Api(value = "kayttajaoikeudet")
public class KayttajaoikeusController {
    @Autowired
    private KayttajaoikeusService service;

    @RequestMapping(value = "/kayttajaoikeudet", method = RequestMethod.GET)
    @ResponseBody
    public List<KayttajaoikeusDto> getYhteisetTyoryhmat(KayttajaoikeusDto oikeusQuery) {
        return service.getKayttooikeudet(oikeusQuery);
    }
}

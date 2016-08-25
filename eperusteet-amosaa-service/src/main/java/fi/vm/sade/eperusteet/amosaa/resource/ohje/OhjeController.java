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
package fi.vm.sade.eperusteet.amosaa.resource.ohje;

import fi.vm.sade.eperusteet.amosaa.dto.ohje.OhjeDto;
import fi.vm.sade.eperusteet.amosaa.service.ohje.OhjeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/ohjeet")
@ApiIgnore
public class OhjeController {

    @Autowired
    private OhjeService service;

    @RequestMapping(method = GET)
    public ResponseEntity<List<OhjeDto>> getOhjeet() {
        return ResponseEntity.ok(service.getOhjeet());
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
    public ResponseEntity editOhje(
            @RequestParam Long id) {
        service.removeOhje(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

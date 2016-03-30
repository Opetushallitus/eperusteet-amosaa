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
package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.ops.TermistoService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author apvilkko
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}")
@InternalApi
public class TermistoController {

    @Autowired
    private TermistoService termistoService;

    @RequestMapping(value = "/termisto", method = GET)
    public ResponseEntity<List<TermiDto>> getAll(
            @PathVariable final Long ktId) {
        return ResponseEntity.ok(termistoService.getTermit(ktId));
    }

    @RequestMapping(value = "/termisto/{termiId}", method = GET)
    public ResponseEntity<TermiDto> get(
            @PathVariable final Long ktId,
            @PathVariable final Long termiId) {
        return ResponseEntity.ok(termistoService.getTermi(ktId, termiId));
    }

    @RequestMapping(value = "/termisto/{avain}/avain", method = GET)
    public ResponseEntity<TermiDto> get(
            @PathVariable final Long ktId,
            @PathVariable final String avain) {
        return ResponseEntity.ok(termistoService.getTermiByAvain(ktId, avain));
    }

    @RequestMapping(value = "/termisto", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TermiDto> addTermi(
            @PathVariable final Long ktId,
            @RequestBody TermiDto dto) {
        dto.setId(null);
        return ResponseEntity.ok(termistoService.addTermi(ktId, dto));
    }

    @RequestMapping(value = "/termisto/{termiId}", method = PUT)
    public ResponseEntity<TermiDto> updateTermi(
            @PathVariable final Long ktId,
            @PathVariable final Long termiId,
            @RequestBody TermiDto dto) {
        dto.setId(termiId);
        return ResponseEntity.ok(termistoService.updateTermi(ktId, dto));
    }

    @RequestMapping(value = "/termisto/{termiId}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTermi(
            @PathVariable final Long ktId,
            @PathVariable final Long termiId) {
        termistoService.deleteTermi(ktId, termiId);
    }
}
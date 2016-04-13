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

import fi.vm.sade.eperusteet.amosaa.dto.teksti.KommenttiDto;
import fi.vm.sade.eperusteet.amosaa.service.teksti.KommenttiService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/tekstit/{tkvId}/kommentit")
//@ApiIgnore
public class KommenttiController {

    @Autowired
    private KommenttiService kommenttiService;

    @RequestMapping(method = GET)
    public ResponseEntity<List<KommenttiDto>> getAll(@PathVariable final long ktId,
                                                     @PathVariable final long opsId,
                                                     @PathVariable final long tkvId) {
        List<KommenttiDto> t = kommenttiService.getAllByTekstikappaleviite(opsId, tkvId);
        return new ResponseEntity<>(t, HttpStatus.OK);
    }

    @RequestMapping(method = POST)
    public ResponseEntity<KommenttiDto> add(@PathVariable final long ktId,
                                            @PathVariable final long opsId,
                                            @PathVariable final long tkvId,
                                            @RequestBody KommenttiDto body) {
        body.setTekstikappaleviiteId(tkvId);
        return new ResponseEntity<>(kommenttiService.add(opsId, body), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<KommenttiDto> get(@PathVariable final long ktId,
                                            @PathVariable final long opsId,
                                            @PathVariable final long tkvId,
                                            @PathVariable final long id) {
        KommenttiDto kommenttiDto = kommenttiService.get(opsId, id);
        return new ResponseEntity<>(kommenttiDto, kommenttiDto == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<KommenttiDto> update(@PathVariable final long ktId,
                                               @PathVariable final long opsId,
                                               @PathVariable final long tkvId,
                                               @PathVariable final long id,
                                               @RequestBody KommenttiDto body) {
        return new ResponseEntity<>(kommenttiService.update(opsId, id, body), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public void delete(@PathVariable final long ktId,
                       @PathVariable final long opsId,
                       @PathVariable final long tkvId,
                       @PathVariable final long id) {
        kommenttiService.delete(opsId, id);
    }
}

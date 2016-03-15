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

package fi.vm.sade.eperusteet.amosaa.resource.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.resource.util.CacheControl;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 *
 * @author iSaul
 */
public interface DokumenttiAbstractController {

    DokumenttiService service();

    DokumenttiTyyppi tyyppi();

    @RequestMapping(method = RequestMethod.POST)
    default ResponseEntity<DokumenttiDto> create(
            @PathVariable final Long baseId,
            @PathVariable final Long id,
            @RequestParam(value = "kieli", defaultValue = "fi") final String kieli) throws DokumenttiException {
        DokumenttiDto dokumenttiDto = service().getDto(id, tyyppi(), Kieli.of(kieli));

        // Jos dokumentti ei löydy valmiiksi niin koitetaan tehdä uusi
        if (dokumenttiDto == null) {
            dokumenttiDto = service().createDtoFor(id, tyyppi(), Kieli.of(kieli));
        }

        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpStatus status = HttpStatus.ACCEPTED;
        int maxTimeInMinutes = 2;

        // Aloitetaan luonti jos luonti ei ole jo päällä tai maksimi luontiaika ylitetty
        if (DateUtils.addMinutes(dokumenttiDto.getAloitusaika(), maxTimeInMinutes).before(new Date())
                || dokumenttiDto.getTila() != DokumenttiTila.LUODAAN) {
            // Vaihdetaan dokumentin tila luonniksi
            service().setStarted(dokumenttiDto);

            // Generoidaan dokumentin datasisältö
            // Asynkroninen metodi
            service().generateWithDto(dokumenttiDto);
        } else {
            status = HttpStatus.FORBIDDEN;
        }

        return new ResponseEntity<>(dokumenttiDto, status);
    }

    @RequestMapping(method = RequestMethod.GET)
    @CacheControl(age = CacheControl.ONE_YEAR, nonpublic = false)
    default ResponseEntity<byte[]> get(
            @PathVariable final Long baseId,
            @PathVariable final Long id,
            @RequestParam(value = "kieli", defaultValue = "fi") final String kieli) {
        byte[] pdfdata = service().get(id, tyyppi(), Kieli.of(kieli));

        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "pdf"));
        headers.set("Content-disposition", "inline; filename=\"" + id + ".pdf\"");
        headers.setContentLength(pdfdata.length);

        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/tila", method = RequestMethod.GET)
    default ResponseEntity<DokumenttiDto> query(@PathVariable final Long baseId,
                                                @PathVariable final Long id,
                                                @RequestParam(value = "kieli", defaultValue = "fi") final String kieli) {
        DokumenttiDto dto = service().getDto(id, tyyppi(), Kieli.of(kieli));
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return ResponseEntity.ok(dto);
        }
    }
}

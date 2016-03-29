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

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.resource.util.CacheControl;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

/**
 *
 * @author iSaul
 */
public interface DokumenttiAbstractController {
    DokumenttiRepository repository();

    DokumenttiService service();

    DokumenttiTyyppi tyyppi();

    @RequestMapping(method = RequestMethod.POST)
    default ResponseEntity<DokumenttiDto> create(
            @PathVariable final Long baseId,
            @PathVariable final Long id,
            @RequestParam(defaultValue = "fi") final String kieli) throws DokumenttiException {
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
        if (dokumenttiDto.getAloitusaika() == null
                ||DateUtils.addMinutes(dokumenttiDto.getAloitusaika(), maxTimeInMinutes).before(new Date())
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
            @RequestParam(defaultValue = "fi") final String kieli) {
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
    default DokumenttiDto query(@PathVariable final Long baseId,
                                                @PathVariable final Long id,
                                                @RequestParam(defaultValue = "fi") final String kieli) {
        return service().getDto(id, tyyppi(), Kieli.of(kieli));
    }

    @Transactional
    @RequestMapping(value = "/lisaaKuva", method=RequestMethod.POST)
    default ResponseEntity<Object> addImage(@PathVariable final Long baseId,
                                            @PathVariable final Long id,
                                            @RequestParam final String tyyppi,
                                            @RequestParam(defaultValue = "fi") final String kieli,
                                            @RequestPart(required = true) final MultipartFile file) throws IOException {

        if (tyyppi != null && !file.isEmpty()) {
            //byte[] image = file.getBytes();

            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            /*int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // Muutetaan kaikkien kuvien väriavaruus RGB:ksi jotta PDF/A validointi menee läpi
            // Asetetaan lisäksi läpinäkyvien kuvien taustaksi valkoinen väri
            BufferedImage tempImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            tempImage.getGraphics().setColor(new Color(255, 255, 255, 0));
            tempImage.getGraphics().fillRect (0, 0, width, height);
            tempImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

            bufferedImage = tempImage;*/

            // Muutetaan kuva PNG:ksi
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            byte[] image = baos.toByteArray();
            baos.close();

            // Tehdään DokumenttiDto jos ei löydy jo valmiina
            DokumenttiDto dokumenttiDto = service().getDto(id, tyyppi(), Kieli.of(kieli));

            if (dokumenttiDto == null) {
                dokumenttiDto = service().createDtoFor(id, tyyppi(), Kieli.of(kieli));
            }

            if (dokumenttiDto == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Haetaan domain dokumentti
            Dokumentti dokumentti = repository().findByYhteisetIdAndKieli(id, Kieli.of(kieli));

            switch (tyyppi) {
                case "kansi":
                    dokumentti.setKansikuva(image);
                    break;
                case "ylatunniste":
                    dokumentti.setYlatunniste(image);
                    break;
                case "alatunniste":
                    dokumentti.setAlatunniste(image);
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            repository().save(dokumentti);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @RequestMapping(value = "/lataaKuva", method=RequestMethod.GET)
    @CacheControl(age = CacheControl.ONE_YEAR, nonpublic = false)
    default ResponseEntity<Object> addImage(@PathVariable final Long baseId,
                                            @PathVariable final Long id,
                                            @RequestParam final String tyyppi,
                                            @RequestParam(defaultValue = "fi") final String kieli) {
        if (tyyppi != null) {
            // Tehdään DokumenttiDto jos ei löydy jo valmiina
            DokumenttiDto dokumenttiDto = service().getDto(id, tyyppi(), Kieli.of(kieli));

            if (dokumenttiDto == null) {
                dokumenttiDto = service().createDtoFor(id, tyyppi(), Kieli.of(kieli));
            }

            if (dokumenttiDto == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Haetaan kuva
            Dokumentti dokumentti = repository().findByYhteisetIdAndKieli(id, Kieli.of(kieli));
            if (dokumentti == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            byte[] image;

            switch (tyyppi) {
                case "kansi":
                    image = dokumentti.getKansikuva();
                    break;
                case "ylatunniste":
                    image = dokumentti.getYlatunniste();
                    break;
                case "alatunniste":
                    image = dokumentti.getAlatunniste();
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (image == null || image.length == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(image.length);

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

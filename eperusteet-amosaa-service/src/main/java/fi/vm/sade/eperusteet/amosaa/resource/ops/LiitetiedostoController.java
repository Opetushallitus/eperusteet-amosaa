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

import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.resource.util.CacheControl;

import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.KUVA_LISAYS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.KUVA_POISTO;

import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.ops.LiiteService;
import io.swagger.annotations.Api;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author jhyoty
 */
@RestController
@Api("liitetiedostot")
@InternalApi
public class LiitetiedostoController extends KoulutustoimijaIdGetterAbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(LiitetiedostoController.class);

    private static final int BUFSIZE = 64 * 1024;
    final private Tika tika = new Tika();

    @Autowired
    private LiiteService liitteet;

    private static final Set<String> SUPPORTED_TYPES;

    static {
        HashSet<String> tmp = new HashSet<>(Arrays.asList(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE));
        SUPPORTED_TYPES = Collections.unmodifiableSet(tmp);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/kuvat", method = RequestMethod.POST)
    public ResponseEntity<String> upload(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @RequestParam String nimi,
            @RequestParam Part file,
            @RequestParam Integer width,
            @RequestParam Integer height,
            UriComponentsBuilder ucb
    ) throws IOException, HttpMediaTypeNotSupportedException {
        LogMessage.builder(OPETUSSUUNNITELMA, KUVA_LISAYS).log();
        final long koko = file.getSize();
        try (PushbackInputStream pis = new PushbackInputStream(file.getInputStream(), BUFSIZE)) {
            byte[] buf = new byte[koko < BUFSIZE ? (int) koko : BUFSIZE];
            int len = pis.read(buf);
            if (len < buf.length) {
                throw new IOException("luku epäonnistui");
            }
            pis.unread(buf);
            String tyyppi = tika.detect(buf);
            if (!SUPPORTED_TYPES.contains(tyyppi)) {
                throw new HttpMediaTypeNotSupportedException(tyyppi + "ei ole tuettu");
            }

            UUID id;
            if (width != null && height != null) {
                ByteArrayOutputStream os = scaleImage(file, tyyppi, width, height);
                id = liitteet.add(ktId, opsId, tyyppi, nimi, os.size(), new PushbackInputStream(new ByteArrayInputStream(os.toByteArray())));
            } else {
                id = liitteet.add(ktId, opsId, tyyppi, nimi, koko, pis);
            }

            HttpHeaders h = new HttpHeaders();
            h.setLocation(ucb.path("/opetussuunnitelmat/{opsId}/kuvat/{id}").buildAndExpand(opsId, id.toString()).toUri());
            return new ResponseEntity<>(id.toString(), h, HttpStatus.CREATED);
        }
    }

    private ByteArrayOutputStream scaleImage(
            @RequestParam("file") Part file,
            String tyyppi,
            Integer width,
            Integer height
    ) throws IOException {
        BufferedImage a = ImageIO.read(file.getInputStream());
        BufferedImage preview = new BufferedImage(width, height, a.getType());
        preview.createGraphics().drawImage(a.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(preview, tyyppi.replace("image/", ""), os);

        return os;
    }

    private BufferedImage scaleImage(BufferedImage img, int maxDimension) {
        int w = (img.getWidth() > img.getHeight() ? maxDimension :
                (int) (((double) img.getWidth() / img.getHeight()) * maxDimension));

        int h = (img.getHeight() > img.getWidth() ? maxDimension :
                (int) (((double) img.getHeight() / img.getWidth()) * maxDimension));

        BufferedImage preview = new BufferedImage(w, h, img.getType());
        preview.createGraphics().drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
        return preview;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/kuvat/{id}", method = RequestMethod.GET)
    @CacheControl(age = CacheControl.ONE_YEAR)
    public void get(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @PathVariable UUID id,
            @RequestHeader(value = "If-None-Match", required = false) String etag,
            HttpServletResponse response
    ) throws IOException {
        get(opsId, id, etag, response);
    }

    @RequestMapping(value = "/opetussuunnitelmat/{opsId}/kuvat/{id}", method = RequestMethod.GET)
    @CacheControl(age = CacheControl.ONE_YEAR)
    public void get(
            @PathVariable Long opsId,
            @PathVariable UUID id,
            @RequestHeader(value = "If-None-Match", required = false) String etag,
            HttpServletResponse response
    ) throws IOException {
        LiiteDto dto = liitteet.get(opsId, id);
        if (dto != null) {
            if (etag != null && dto.getId().toString().equals(etag)) {
                response.setStatus(HttpStatus.NOT_MODIFIED.value());
            } else {
                response.setHeader("Content-Type", dto.getTyyppi());
                response.setHeader("ETag", id.toString());
                try (OutputStream os = response.getOutputStream()) {
                    liitteet.export(opsId, id, os);
                    os.flush();
                }
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/kuvat/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId,
            @PathVariable UUID id
    ) {
        LogMessage.builder(OPETUSSUUNNITELMA, KUVA_POISTO).log();
        liitteet.delete(ktId, opsId, id);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/kuvat", method = RequestMethod.GET)
    public List<LiiteDto> getAll(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId
    ) {
        return liitteet.getAll(ktId, opsId);
    }
}

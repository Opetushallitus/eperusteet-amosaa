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

package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiEdistyminen;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;


/**
 *
 * @author iSaul
 */
@Service
@Transactional
public class DokumenttiServiceImpl implements DokumenttiService {
    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiServiceImpl.class);

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private DokumenttiBuilderService builder;

    @Override
    public DokumenttiDto getDto(Long id, Kieli kieli) {
        Dokumentti dokumentti = dokumenttiRepository.findByOpsIdAndKieli(id, kieli);

        if (dokumentti != null) {
            return mapper.map(dokumentti, DokumenttiDto.class);
        } else {
            return createDtoFor(id, kieli);
        }
    }

    @Override
    public DokumenttiDto createDtoFor(Long id, Kieli kieli) {
        Dokumentti dokumentti = new Dokumentti();
        dokumentti.setTila(DokumenttiTila.EI_OLE);
        dokumentti.setKieli(kieli);

        if (opsRepository.findOne(id) != null) {
            dokumentti.setOpsId(id);
            Dokumentti saved = dokumenttiRepository.save(dokumentti);
            return mapper.map(saved, DokumenttiDto.class);
        }

        return null;
    }

    @Override
    public void setStarted(DokumenttiDto dto) {
        // Asetetaan dokumentti luonti tilaan
        dto.setAloitusaika(new Date());
        dto.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());
        dto.setTila(DokumenttiTila.LUODAAN);
        dto.setVirhekoodi("");
        dokumenttiRepository.save(mapper.map(dto, Dokumentti.class));
    }

    @Override
    public void generateWithDto(DokumenttiDto dto) throws DokumenttiException {
        Dokumentti dokumentti = mapper.map(dto, Dokumentti.class);

        try {
            // Luodaan pdf
            if (dokumentti.getOpsId()!= null) {
                Opetussuunnitelma ops = opsRepository.findOne(dokumentti.getOpsId());
                dokumentti.setData(builder.generatePdf(ops, dokumentti, dokumentti.getKieli()));
            } else if (dokumentti.getOpsId() != null) {
                LOG.info("Ops is not implemented yet");
            }

            dokumentti.setTila(DokumenttiTila.VALMIS);
            dokumentti.setValmistumisaika(new Date());
            dokumentti.setVirhekoodi("");
            dokumentti.setEdistyminen(DokumenttiEdistyminen.TUNTEMATON);

            // Tallennetaan valmis dokumentti
            dokumenttiRepository.save(dokumentti);
        } catch (Exception ex) {
            dokumentti.setTila(DokumenttiTila.EPAONNISTUI);
            dokumentti.setVirhekoodi(ExceptionUtils.getStackTrace(ex));
            dokumenttiRepository.save(dokumentti);

            throw new DokumenttiException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] get(Long id, Kieli kieli) {
        Dokumentti dokumentti = dokumenttiRepository.findByOpsIdAndKieli(id, kieli);
        if (dokumentti != null) {
            return dokumentti.getData();
        }

        return null;
    }

    public boolean addImage(DokumenttiDto dto, String tyyppi, String kieli, MultipartFile file) throws IOException {

        if (!file.isEmpty()) {

            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // Muutetaan kaikkien kuvien väriavaruus RGB:ksi jotta PDF/A validointi menee läpi
            // Asetetaan lisäksi läpinäkyvien kuvien taustaksi valkoinen väri
            BufferedImage tempImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            tempImage.getGraphics().setColor(new Color(255, 255, 255, 0));
            tempImage.getGraphics().fillRect (0, 0, width, height);
            tempImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

            bufferedImage = tempImage;

            // Muutetaan kuva PNG:ksi
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            baos.flush();
            byte[] image = baos.toByteArray();
            baos.close();

            // todo: tarkista onko tiedosto sallittu kuva

            // Haetaan domain dokumentti
            Dokumentti dokumentti = dokumenttiRepository.findByOpsIdAndKieli(dto.getOpsId(), Kieli.of(kieli));

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
                    return false;
            }

            dokumenttiRepository.save(dokumentti);
            return true;
        }

        return false;
    }
}

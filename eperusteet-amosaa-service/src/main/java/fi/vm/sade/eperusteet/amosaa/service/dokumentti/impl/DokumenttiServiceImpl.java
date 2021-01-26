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
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiStateService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


/**
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
    private DokumenttiBuilderService builderService;

    @Autowired
    private DokumenttiStateService dokumenttiStateService;

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto getDto(Long ktId, Long opsId, Kieli kieli) {
        Dokumentti dokumentti = getLatestDokumentti(opsId, kieli);

        if (dokumentti != null) {

            // Jos aloitusajasta on kulunut liian kauan, on luonti epäonnistunut
            if (dokumentti.getTila() != DokumenttiTila.VALMIS && dokumentti.getTila() != DokumenttiTila.EI_OLE) {
                if (DokumenttiUtils.isTimePass(dokumentti)) {
                    dokumentti.setTila(DokumenttiTila.EPAONNISTUI);
                    dokumentti = dokumenttiRepository.save(dokumentti);
                }
            }

            return mapper.map(dokumentti, DokumenttiDto.class);
        } else {
            return createDtoFor(ktId, opsId, kieli);
        }
    }

    private Dokumentti getLatestDokumentti(Long opsId, Kieli kieli) {
        List<Dokumentti> dokumentit = dokumenttiRepository.findByOpsIdAndKieli(opsId, kieli);
        if (dokumentit.isEmpty()) {
            return null;
        } else {
            return dokumentit.get(0);
        }
    }

    @Override
    public DokumenttiDto update(Long ktId, Long opsId, Kieli kieli, DokumenttiDto dto) {
        Dokumentti dokumentti = getLatestDokumentti(opsId, kieli);
        if (dokumentti == null) {
            return null;
        } else {
            mapper.map(dto, dokumentti);
            return mapper.map(dokumentti, DokumenttiDto.class);
        }
    }


    @Override
    public DokumenttiDto createDtoFor(Long ktId, Long id, Kieli kieli) {
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
    public void setStarted(Long ktId, Long opsId, DokumenttiDto dto) {
        // Asetetaan dokumentti luonti tilaan
        dto.setAloitusaika(new Date());
        dto.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());
        dto.setTila(DokumenttiTila.JONOSSA);
        dokumenttiStateService.save(dto);
    }

    @Override
    @Transactional(noRollbackFor = DokumenttiException.class)
    @Async(value = "docTaskExecutor")
    public void generateWithDto(Long ktId, @NotNull Long opsId, DokumenttiDto dto) throws DokumenttiException {
        dto.setTila(DokumenttiTila.LUODAAN);
        dokumenttiStateService.save(dto);

        Dokumentti dokumentti = dokumenttiRepository.findOne(dto.getId());
        try {
            Opetussuunnitelma ops = opsRepository.findOne(dokumentti.getOpsId());
            dokumentti.setTila(DokumenttiTila.VALMIS);
            dokumentti.setValmistumisaika(new Date());
            dokumentti.setVirhekoodi("");
            dokumentti.setEdistyminen(DokumenttiEdistyminen.TUNTEMATON);
            dokumentti.setData(builderService.generatePdf(ops, dokumentti, dokumentti.getKieli()));

            dokumenttiRepository.save(dokumentti);
        } catch (Exception ex) {
            dokumentti.setTila(DokumenttiTila.EPAONNISTUI);
            dokumentti.setVirhekoodi(ex.getLocalizedMessage());
            dokumenttiRepository.save(dokumentti);

            throw new DokumenttiException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] get(Long ktId, Long id, Kieli kieli) {
        Dokumentti dokumentti = getLatestDokumentti(id, kieli);
        if (dokumentti != null) {
            return dokumentti.getData();
        }

        return null;
    }

    @Override
    public DokumenttiDto addImage(Long ktId, Long opsId, DokumenttiDto dto, String tyyppi, String kieli, MultipartFile file) throws IOException {

        if (!file.isEmpty()) {

            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // Muutetaan kaikkien kuvien väriavaruus RGB:ksi jotta PDF/A validointi menee läpi
            // Asetetaan lisäksi läpinäkyvien kuvien taustaksi valkoinen väri
            BufferedImage tempImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            tempImage.getGraphics().setColor(new Color(255, 255, 255, 0));
            tempImage.getGraphics().fillRect(0, 0, width, height);
            tempImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

            bufferedImage = tempImage;

            // Muutetaan kuva PNG:ksi
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            baos.flush();
            byte[] image = baos.toByteArray();
            baos.close();

            // Todo: Tarkista onko tiedosto sallittu kuva

            // Haetaan domain dokumentti
            Dokumentti dokumentti = getLatestDokumentti(dto.getOpsId(), Kieli.of(kieli));

            switch (tyyppi) {
                case "kansikuva":
                    dokumentti.setKansikuva(image);
                    break;
                case "ylatunniste":
                    dokumentti.setYlatunniste(image);
                    break;
                case "alatunniste":
                    dokumentti.setAlatunniste(image);
                    break;
                default:
                    return null;
            }

            return mapper.map(dokumenttiRepository.save(dokumentti), DokumenttiDto.class);
        }

        return null;
    }
}

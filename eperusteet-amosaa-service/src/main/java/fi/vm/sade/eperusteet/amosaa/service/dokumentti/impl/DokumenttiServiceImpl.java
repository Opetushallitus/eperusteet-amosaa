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
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Yhteiset;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.YhteisetRepository;
import fi.vm.sade.eperusteet.amosaa.resource.dokumentti.util.DokumenttiTyyppi;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 * @author iSaul
 */
@Service
public class DokumenttiServiceImpl implements DokumenttiService {
    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiServiceImpl.class);

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private YhteisetRepository yhteisetRepository;

    @Autowired
    private DokumenttiBuilderService builder;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public DokumenttiDto getDto(Long id, DokumenttiTyyppi tyyppi, Kieli kieli) {
        Dokumentti dokumentti = null;
        if (tyyppi == DokumenttiTyyppi.YHTEISET) {
            dokumentti = dokumenttiRepository.findByYhteisetIdAndKieli(id, kieli);
        } else if (tyyppi == DokumenttiTyyppi.OPS) {
            dokumentti = dokumenttiRepository.findByOpsIdAndKieli(id, kieli);
        }

        if (dokumentti != null) {
            return mapper.map(dokumentti, DokumenttiDto.class);
        }

        return null;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public DokumenttiDto createDtoFor(Long id, DokumenttiTyyppi tyyppi, Kieli kieli) {
        Dokumentti dokumentti = new Dokumentti();
        dokumentti.setTila(DokumenttiTila.EI_OLE);
        dokumentti.setAloitusaika(new Date());
        dokumentti.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());
        dokumentti.setKieli(kieli);
        if (tyyppi == DokumenttiTyyppi.YHTEISET && yhteisetRepository.findOne(id) != null) {
            dokumentti.setYhteisetId(id);
            Dokumentti saved = dokumenttiRepository.save(dokumentti);
            return mapper.map(saved, DokumenttiDto.class);
        } else if (tyyppi == DokumenttiTyyppi.OPS) {
            dokumentti.setOpsId(id);
        }

        return null;
    }

    @Override
    @Transactional
    public void setStarted(DokumenttiDto dto) {
        // Asetetaan dokumentti luonti tilaan
        dto.setAloitusaika(new Date());
        dto.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());
        dto.setTila(DokumenttiTila.LUODAAN);
        dokumenttiRepository.save(mapper.map(dto, Dokumentti.class));
    }

    @Override
    @Transactional
    public void generateWithDto(DokumenttiDto dto) throws DokumenttiException {
        Dokumentti dokumentti = mapper.map(dto, Dokumentti.class);

        try {
            // Luodaan pdf
            if (dokumentti.getYhteisetId() != null) {
                Yhteiset yhteiset = yhteisetRepository.findOne(dokumentti.getYhteisetId());
                dokumentti.setData(builder.generatePdf(yhteiset, dokumentti, dokumentti.getKieli()));
            } else if (dokumentti.getOpsId() != null) {
                LOG.info("Ops is not implemented yet");
            }
            dokumentti.setTila(DokumenttiTila.VALMIS);
            dokumentti.setValmistumisaika(new Date());
            dokumentti.setVirhekoodi("");

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
    public byte[] get(Long id, DokumenttiTyyppi tyyppi, Kieli kieli) {
        if (tyyppi == DokumenttiTyyppi.YHTEISET) {
            Dokumentti dokumentti = dokumenttiRepository.findByYhteisetIdAndKieli(id, kieli);
            if (dokumentti != null) {
                return dokumentti.getData();
            }
        } else if (tyyppi == DokumenttiTyyppi.OPS) {
            LOG.info("Ops is not implemented yet");
        }

        return null;
    }
}

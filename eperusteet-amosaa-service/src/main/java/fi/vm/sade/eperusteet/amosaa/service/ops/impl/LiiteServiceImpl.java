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
package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;
import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.liite.LiiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.amosaa.service.exception.ServiceException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.LiiteService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jhyoty
 */
@Service
@Transactional
public class LiiteServiceImpl implements LiiteService {
    @Autowired
    private LiiteRepository liiteRepository;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    DtoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public void export(Long ktId, UUID id, OutputStream os) {
        Liite liite = liiteRepository.findOne(id);
        if ( liite == null ) {
            throw new NotExistsException("ei ole");
        }
        try ( InputStream is = liite.getData().getBinaryStream() ) {
            IOUtils.copy(is, os);
        } catch (SQLException | IOException ex) {
            throw new ServiceException("Liiteen lataaminen ei onnistu", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LiiteDto get(Long ktId, UUID id) {
        Liite liite = liiteRepository.findOne(id);
        return mapper.map(liite, LiiteDto.class);
    }

    @Override
    public UUID add(Long ktId, String tyyppi, String nimi, long length, InputStream is) {
        Liite liite = liiteRepository.add(tyyppi, nimi, length, is);
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        kt.attachLiite(liite);
        return liite.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiiteDto> getAll(Long ktId) {
        return mapper.mapAsList(liiteRepository.findByKoulutustoimijaId(ktId), LiiteDto.class);
    }

    @Override
    public void delete(Long ktId, UUID id) {
        Liite liite = liiteRepository.findOne(ktId, id);
        if ( liite == null ) {
            throw new NotExistsException("Liitettä ei ole");
        }
        koulutustoimijaRepository.findOne(ktId).removeLiite(liite);
    }
}

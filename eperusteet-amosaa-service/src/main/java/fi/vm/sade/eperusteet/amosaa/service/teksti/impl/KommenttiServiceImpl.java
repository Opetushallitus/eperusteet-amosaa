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
package fi.vm.sade.eperusteet.amosaa.service.teksti.impl;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kommentti;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KommenttiDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.KommenttiRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionManager;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionManager.Permission;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionManager.TargetType;
import fi.vm.sade.eperusteet.amosaa.service.teksti.KommenttiService;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author mikkom
 */
@Service
@Transactional(readOnly = false)
public class KommenttiServiceImpl implements KommenttiService {

    @Autowired
    private KommenttiRepository repository;

    @Autowired
    private KayttajanTietoService kayttajat;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private PermissionManager permissionManager;

    @Autowired
    KayttajanTietoService kayttajanTietoService;

    @Override
    @Transactional(readOnly = true)
    public List<KommenttiDto> getAllByTekstikappaleviite(Long opsId, Long tkvId) {
        List<Kommentti> kommentit = repository.findByTekstikappaleviiteIdOrderByLuotuDesc(tkvId);
        List<KommenttiDto> kommenttiDtos = mapper.mapAsList(kommentit, KommenttiDto.class);
        return addNameToKommentit(kommenttiDtos);
    }

    private List<KommenttiDto> addNameToKommentit(List<KommenttiDto> kommentit) {
        return kommentit.stream()
                .map(this::addNameToKommentti)
                .map(this::hideRemovedSisalto)
                .collect(Collectors.toList());
    }

    private KommenttiDto addNameToKommentti(KommenttiDto kommenttiDto) {
        KayttajanTietoDto kayttaja = kayttajanTietoService.hae(kommenttiDto.getMuokkaaja());
        if (kayttaja != null) {
            String kutsumanimi = kayttaja.getKutsumanimi();
            String etunimet = kayttaja.getEtunimet();
            String etunimi = kutsumanimi != null ? kutsumanimi : etunimet;
            String sukunimi = kayttaja.getSukunimi();
            if (etunimi != null && sukunimi != null) {
                kommenttiDto.setNimi(etunimi + " " + kayttaja.getSukunimi());
            } else {
                kommenttiDto.setNimi(kayttaja.getOidHenkilo());
            }
        }
        return kommenttiDto;
    }

    private KommenttiDto hideRemovedSisalto(KommenttiDto kommenttiDto) {
        if (kommenttiDto.getPoistettu()) {
            kommenttiDto.setSisalto(null);
        }
        return  kommenttiDto;
    }

    @Override
    @Transactional(readOnly = true)
    public KommenttiDto get(Long opsId, Long kommenttiId) {
        Kommentti kommentti = repository.findOne(kommenttiId);
        return mapper.map(kommentti, KommenttiDto.class);
    }

    @Override
    @Transactional
    public KommenttiDto add(@P("opsId") Long opsId, KommenttiDto kommenttiDto) {
        Kommentti kommentti = mapper.map(kommenttiDto, Kommentti.class);
        kommentti.setSisalto(clip(kommenttiDto.getSisalto()));
        kommentti.setPoistettu(false);
        kommentti = repository.save(kommentti);

        KommenttiDto uusiDto = mapper.map(repository.save(kommentti), KommenttiDto.class);
        return addNameToKommentti(uusiDto);
    }

    private static String clip(String kommentti) {
        if (kommentti != null) {
            int length = kommentti.length();
            return kommentti.substring(0, Math.min(length, 1024));
        } else {
            return "";
        }
    }

    private void assertRights(Kommentti kommentti, Long opsId, Permission p) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KayttajanTietoDto kirjautunut = kayttajat.haeKirjautaunutKayttaja();
        if (kirjautunut.getOidHenkilo().equals(kommentti.getLuoja())) {
            return;
        }
        if (!permissionManager.hasPermission(authentication, opsId, TargetType.OPETUSSUUNNITELMA, p)) {
            throw new BusinessRuleViolationException("Ei oikeutta");
        }
    }

    @Override
    public KommenttiDto update(Long opsId, Long kommenttiId, KommenttiDto kommenttiDto) {
        Kommentti kommentti = repository.findOne(kommenttiId);
        assertExists(kommentti, "Päivitettävää kommenttia ei ole olemassa");
        assertRights(kommentti, opsId, Permission.HALLINTA);
        kommentti.setSisalto(clip(kommenttiDto.getSisalto()));
        KommenttiDto uusiDto = mapper.map(repository.save(kommentti), KommenttiDto.class);
        return addNameToKommentti(uusiDto);
    }

    @Override
    public void delete(Long opsId, Long kommenttiId) {
        Kommentti kommentti = repository.findOne(kommenttiId);
        assertExists(kommentti, "Poistettavaa kommenttia ei ole olemassa");
        assertRights(kommentti, opsId, Permission.HALLINTA);
        kommentti.setPoistettu(true);
        repository.save(kommentti);
    }
}

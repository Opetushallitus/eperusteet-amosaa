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

package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nkala
 */
@Service
@Transactional(readOnly = true)
public class KayttajaoikeusServiceImpl implements KayttajaoikeusService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KayttajanTietoService ktService;

    @Autowired
    private PermissionManager permissionManager;

    @Override
    public List<KayttajaoikeusDto> getKayttooikeudet() {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(ktService.getUserOid());
        List<Kayttajaoikeus> result = kayttajaoikeusRepository.findAllByKayttaja(kayttaja);
        return mapper.mapAsList(result, KayttajaoikeusDto.class);
    }

    @Override
    public ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<Long>>> getOrganisaatiooikeudet() {
        return ResponseEntity.ok(permissionManager.getOrganisaatioOikeudet());
    }

}

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
package fi.vm.sade.eperusteet.amosaa.service.mocks;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author nkala
 */
@Service
@SuppressWarnings("TransactionalAnnotations")
public class EperusteetServiceMock implements EperusteetService {

    public static final String DIAARINUMERO = "mock-diaarinumero";

    @Override
    public PerusteKaikkiDto getPerusteSisalto(CachedPeruste cperuste) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PerusteDto getYleinenPohja() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Autowired
    private DtoMapper mapper;

    @Override
    public PerusteDto getPeruste(Long id) {
        throw new UnsupportedOperationException("Toteuta");
    }

    @Override
    public String getPerusteData(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PerusteDto getPeruste(String diaariNumero) {
        throw new UnsupportedOperationException("Toteuta");
    }

    @Override
    public List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        PerusteInfoDto perusteInfo = new PerusteInfoDto();
        perusteInfo.setDiaarinumero(DIAARINUMERO);
        return Collections.singletonList(perusteInfo);
    }

    @Override
    public List<PerusteInfoDto> findPerusteet() {
        return findPerusteet(null);
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        return null;
    }

}

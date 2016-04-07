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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.ReferenceNamingStrategy;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.impl.perustedto.EperusteetPerusteDto;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.io.IOException;
import java.io.InputStream;
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
    private EperusteetPerusteDto perusteDto = null;

    @Autowired
    private DtoMapper mapper;

    @Override
    public PerusteDto getPeruste(Long id) {
        throw new UnsupportedOperationException("Toteuta");
    }

    @Override
    public String getPerusteString(String diaarinumero) {
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

    public void setPeruste(InputStream is) throws IOException {
        if (is == null) {
            perusteDto = null;
        } else {
            final ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            om.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            om.registerModule(new Jdk8Module());
            om.setPropertyNamingStrategy(new ReferenceNamingStrategy());
            perusteDto = om.readValue(is, EperusteetPerusteDto.class);
        }
    }

}

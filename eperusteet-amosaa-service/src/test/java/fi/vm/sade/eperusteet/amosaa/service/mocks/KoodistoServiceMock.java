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

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KoodistoService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author mikkom
 */
@Service
public class KoodistoServiceMock implements KoodistoService {
    @Override
    public List<KoodistoKoodiDto> getAll(String koodisto) {
        return Collections.emptyList();
    }

    @Override
    public KoodistoKoodiDto get(String koodisto, String koodi) {
        return null;
    }

    @Override
    public List<KoodistoKoodiDto> filterBy(String koodisto, String haku) {
        return Collections.emptyList();
    }

    @Override
    public List<KoodistoKoodiDto> getAlarelaatio(String koodi) {
        return Collections.emptyList();
    }

    @Override
    public List<KoodistoKoodiDto> getYlarelaatio(String koodi) {
        return Collections.emptyList();
    }
}

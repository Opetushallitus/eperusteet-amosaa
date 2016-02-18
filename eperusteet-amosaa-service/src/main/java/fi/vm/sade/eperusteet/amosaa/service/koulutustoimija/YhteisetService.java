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

package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteisetSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author nkala
 */
public interface YhteisetService extends RevisionService {

    @PreAuthorize("isAuthenticated()")
    YhteisetDto getYhteiset(Long kid, Long id);

    @PreAuthorize("isAuthenticated()")
    List<PoistettuDto> getYhteisetPoistetut(Long kid, Long id);

    @PreAuthorize("isAuthenticated()")
    YhteisetSisaltoDto getYhteisetSisalto(Long kid, Long id);

    @PreAuthorize("isAuthenticated()")
    YhteisetDto updateYhteiset(Long kid, Long id, YhteisetDto body);
}

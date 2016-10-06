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

package fi.vm.sade.eperusteet.amosaa.service.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import java.io.IOException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author iSaul
 */
public interface DokumenttiService {

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    DokumenttiDto getDto(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    DokumenttiDto createDtoFor(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    void setStarted(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    @Async(value = "docTaskExecutor")
    void generateWithDto(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto) throws DokumenttiException;

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    DokumenttiDto addImage(@P("ktId") Long ktId, @P("opsId") Long opsId, DokumenttiDto dto, String tyyppi, String kieli, MultipartFile image) throws IOException;

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    byte[] get(@P("ktId") Long ktId, @P("opsId") Long opsId, Kieli kieli);

}

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

package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Collection;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author nkala
 */
@PreAuthorize("permitAll()")
public interface KoodistoClient {
    List<KoodistoKoodiDto> getAll(String koodisto);

    KoodistoKoodiDto get(String koodisto, String koodi);

    List<KoodistoKoodiDto> queryByKoodi(String koodisto, String koodi);

    KoodistoKoodiDto getByUri(String uri);

    List<KoodistoKoodiDto> filterBy(String koodisto, String haku);

    List<KoodistoKoodiDto> getAlarelaatio(String koodi);

    List<KoodistoKoodiDto> getYlarelaatio(String koodi);

    @PreAuthorize("isAuthenticated()")
    KoodistoKoodiDto addKoodi(KoodistoKoodiDto koodi);

    @PreAuthorize("isAuthenticated()")
    KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi);

    @PreAuthorize("isAuthenticated()")
    KoodistoKoodiDto addKoodiNimella(String koodistonimi, LokalisoituTekstiDto koodinimi, long seuraavaKoodi);

    @PreAuthorize("isAuthenticated()")
    long nextKoodiId(String koodistonimi);

    @PreAuthorize("isAuthenticated()")
    Collection<Long> nextKoodiId(String koodistonimi, int count);
}

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
package fi.vm.sade.eperusteet.amosaa.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaSuoritustapaDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author nkala
 */
@Service
@PreAuthorize("permitAll()") // OK, koska mäppääntyy julkisiin rajapintoihin
public interface EperusteetService {
    JsonNode getTutkinnonOsat(Long id);

    List<TutkinnonOsaSuoritustapaDto> convertTutkinnonOsat(JsonNode tutkinnonosat);

    JsonNode getSuoritustavat(Long id);

    JsonNode getTutkinnonOsa(Long id, Long tosaId);

    JsonNode getSuoritustapa(Long id, String tyyppi);

    <T> T getPerusteSisalto(Long cperusteId, Class<T> type);

    <T> T getPerusteSisalto(CachedPeruste cperuste, Class<T> type);

    List<PerusteDto> findPerusteet();
//    <T> T getPeruste(String diaariNumero, Class<T> type);
//
//    PerusteDto getYleinenPohja();
//
//    String getYleinenPohjaSisalto();
//
//    String getPerusteData(Long id);
//
//    List<PerusteDto> findPerusteet();
//
//    List<PerusteDto> findPerusteet(Set<KoulutusTyyppi> tyypit);
//
//    JsonNode getTiedotteet(Long jalkeen);
//
//    List<TutkinnonOsaSuoritustapaDto> convertTutkinnonOsat(JsonNode tutkinnonosat);
//
//    <T> T getPeruste(Long id, Class<T> type);
//
//    <T> T getPerusteSisalto(Long cperusteId, Class<T> type);
//
//    <T> T getPerusteSisalto(CachedPeruste cperuste, Class<T> type);
//
//    JsonNode getTutkinnonOsat(Long id);
//
//    JsonNode getSuoritustavat(Long id);
//
//    JsonNode getTutkinnonOsa(Long id, Long tosaId);
//
//    JsonNode getSuoritustapa(Long id, String tyyppi);
//
//    ArviointiasteikkoDto getArviointiasteikko(Long id);
}

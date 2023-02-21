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
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author nkala
 */
@Service
@PreAuthorize("permitAll()") // OK, koska mäppääntyy julkisiin rajapintoihin
public interface EperusteetService {
    @PreAuthorize("isAuthenticated()")
    CachedPerusteBaseDto getCachedPeruste(PerusteBaseDto peruste);

    JsonNode getTutkinnonOsat(Long id);

    List<TutkinnonOsaSuoritustapaDto> convertTutkinnonOsat(JsonNode tutkinnonosat);

    JsonNode getSuoritustavat(Long id);
    
    List<RakenneModuuliTunnisteDto> getSuoritustavat(Long ktId, Long opetussuunnitelmaId);
    
    RakenneModuuliTunnisteDto getYksittaisenRakenteenSuoritustavat(SuoritustapaLaajaDto suoritustapaLaajaDto, SisaltoViiteDto sisaltoViite);

    JsonNode getTutkinnonOsa(Long id, Long tosaId);

    JsonNode getSuoritustapa(Long id, String tyyppi);

    JsonNode getTutkinnonOsaViite(Long id, String tyyppi, Long tosaId);

    JsonNode getTutkinnonOsaViitteet(Long id, String tyyppi);

    <T> T getPerusteSisalto(Long cperusteId, Class<T> type);

    <T> T getPerusteSisalto(CachedPeruste cperuste, Class<T> type);

    List<PerusteDto> findPerusteet();

    <T> List<T> findPerusteet(Class<T> type);

    <T> List<T> findPerusteet(Set<String> koulutustyypit, Class<T> type);

    <T> T getPerusteSisaltoByPerusteId(Long perusteId, Class<T> type);

    Set<UUID> getRakenneTunnisteet(Long id, String suoritustapa);

    JsonNode getTiedotteet(Long jalkeen);

    JsonNode getGeneeriset();

    JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto);

    byte[] getLiite(Long perusteId, UUID id);

    PerusteenOsaDto getPerusteenOsa(Long perusteId, Long perusteenOsaId);

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
//    JsonNode getTutkinnonOsaViitteet(Long id);
//
//    JsonNode getSuoritustavat(Long id);
//
//    JsonNode getTutkinnonOsa(Long id, Long tosaId);
//
//    JsonNode getSuoritustapa(Long id, String tyyppi);
//
//    ArviointiasteikkoDto getArviointiasteikko(Long id);
}

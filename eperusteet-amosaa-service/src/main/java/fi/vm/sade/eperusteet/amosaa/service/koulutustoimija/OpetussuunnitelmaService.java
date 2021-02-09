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

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.VanhentunutPohjaperusteDto;
import fi.vm.sade.eperusteet.amosaa.service.revision.RevisionService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author nkala
 */
public interface OpetussuunnitelmaService extends RevisionService {

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaBaseDto> getPohjat();

    @PreAuthorize("isAuthenticated()")
    void mapPerusteIds();

    @PreAuthorize("isAuthenticated()")
    void mapKoulutustyyppi();

    @PreAuthorize("isAuthenticated()")
    void mapKoulutukset();

    @PreAuthorize("permitAll()")
    List<OpetussuunnitelmaDto> getJulkisetOpetussuunnitelmat(Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    Page<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(@P("ktId") Long ktId, PageRequest page, OpsHakuDto query);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    <T> Page<T> getOpetussuunnitelmat(@P("ktId") Long ktId, PageRequest page, OpsHakuDto query, Class<T> clazz);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS')")
    KoulutustoimijaJulkinenDto getKoulutustoimijaId(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    OpetussuunnitelmaDto getOpetussuunnitelma(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<PoistettuDto> getPoistetut(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<KayttajaoikeusDto> getOikeudet(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'HALLINTA')")
    KayttajaoikeusDto updateOikeus(@P("ktId") Long ktId, @P("opsId") Long opsId, Long oikeusId, KayttajaoikeusDto oikeus);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto update(@P("ktId") Long ktId, @P("opsId") Long opsId, OpetussuunnitelmaDto body);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto revertTo(@P("ktId") Long ktId, @P("opsId") Long opsId, Integer revId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'HALLINTA')")
    void paivitaPeruste(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'HALLINTA')")
    List<VanhentunutPohjaperusteDto> haePaivitystaVaativatPerusteet(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUONTI')")
    OpetussuunnitelmaBaseDto addOpetussuunnitelma(@P("ktId") Long ktId, OpetussuunnitelmaLuontiDto opsDto);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'HALLINTA')")
    OpetussuunnitelmaBaseDto updateTila(@P("ktId") Long ktId, @P("opsId") Long opsId, Tila tila, boolean generatePdf);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
    Validointi validoi(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOtherOpetussuunnitelmat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    JsonNode getOpetussuunnitelmanPeruste(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("permitAll()")
    <T extends OpetussuunnitelmaBaseDto> List<T> getPerusteenOpetussuunnitelmat(String diaari, Class<T> type);

    @PreAuthorize("permitAll()")
    <T extends OpetussuunnitelmaBaseDto> List<T> getJulkaistutPerusteenOpetussuunnitelmat(String diaari, Class<T> type);

    @PreAuthorize("permitAll()")
    Page<OpetussuunnitelmaDto> findOpetussuunnitelmat(PageRequest p, OpetussuunnitelmaQueryDto pquery);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'HALLINTA')")
    OpetussuunnitelmaDto updateKoulutustoimija(@P("ktId") Long ktId, @P("opsId") Long opsId, KoulutustoimijaBaseDto body);
    
    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    OpetussuunnitelmaDto updateKoulutustoimijaPassivoidusta(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaKaikki(@P("ktId") Long ktId, @P("opsId") Long opsId);

    @PreAuthorize("hasPermission(null, 'OPH','HALLINTA')")
    void updateOpetussuunnitelmaSisaltoviitePiilotukset();

    @PreAuthorize("isAuthenticated()")
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmatOrganisaatioista(String organisaatioId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    NavigationNodeDto buildNavigation(Long ktId, Long opsId);

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'ESITYS')")
    NavigationNodeDto buildNavigationPublic(Long ktId, Long opsId);

    @PreAuthorize("hasPermission(null, 'OPH','HALLINTA')")
    List<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot();

    @PreAuthorize("hasPermission(null, 'OPH','HALLINTA')")
    Page<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot(Integer sivu, Integer sivukoko);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId, OpsTyyppi tyyppi);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId, Set<String> koulutustyypit);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaDto> getOpetussuunnitelmat(@P("ktId") Long ktId, Set<String> koulutustyypit, OpsTyyppi opsTyyppi);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<OpetussuunnitelmaBaseDto> getPohjat(Long ktId, Set<Tila> tilat, Set<KoulutusTyyppi> koulutustyypit, OpsTyyppi opsTyyppi);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaBaseDto> getOphOpsPohjat(Set<KoulutusTyyppi> koulutustyypit);
}


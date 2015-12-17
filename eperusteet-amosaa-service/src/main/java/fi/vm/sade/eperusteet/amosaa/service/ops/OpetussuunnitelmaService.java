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
package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaStatistiikkaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import java.util.List;

import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author mikkom
 */
public interface OpetussuunnitelmaService {

//    @PreAuthorize("(hasPermission(null, 'opetussuunnitelma', 'LUKU'))")
    @PreAuthorize("permitAll()")
    List<OpetussuunnitelmaInfoDto> getAll();

//    @PreAuthorize("permitAll()")
//    List<OpetussuunnitelmaJulkinenDto> getAllJulkiset();

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaJulkinenDto getOpetussuunnitelmaJulkinen(@P("opsId") Long id);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    List<OpetussuunnitelmaStatistiikkaDto> getStatistiikka();

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaKevytDto getOpetussuunnitelma(@P("opsId") Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaDto getOpetussuunnitelmaKaikki (@P("opsId") Long id);

    @PreAuthorize("hasPermission(null, 'opetussuunnitelma', 'LUONTI')")
    OpetussuunnitelmaDto addOpetussuunnitelma(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto updateOpetussuunnitelma(@P("ops") OpetussuunnitelmaDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'TILANVAIHTO')")
    OpetussuunnitelmaDto updateTila(@P("id") Long id, Tila tila);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'HALLINTA')")
    OpetussuunnitelmaDto restore(@P("id") Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    void removeOpetussuunnitelma(@P("opsId") Long id);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    List<OpetussuunnitelmaInfoDto> getLapsiOpetussuunnitelmat(Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateLapsiOpetussuunnitelmat(Long opsId);

    @PreAuthorize("hasPermission(#pohjaId, 'opetussuunnitelma', 'MUOKKAUS')")
    void syncPohja(Long pohjaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T getTekstit(@P("opsId") final Long opsId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappale(@P("opsId") final Long opsId, TekstiKappaleViiteDto.Matala viite);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(@P("opsId") final Long opsId, final Long parentId,
                                                       TekstiKappaleViiteDto.Matala viite);
    /**
     * Hakee opetussuunnitelmaan liittyvän opetussuunnitelman perusteen
     *
     * @param opsId
     * @return Peruste
     */
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    public PerusteDto getPeruste(@P("opsId") Long opsId);
}

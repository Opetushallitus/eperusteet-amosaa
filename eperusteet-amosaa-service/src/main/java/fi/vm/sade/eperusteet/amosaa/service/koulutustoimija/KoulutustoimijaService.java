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

import fi.vm.sade.eperusteet.amosaa.dto.TiedoteDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.TyoryhmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YhteinenSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaDto;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author nkala
 */
// FIXME Oikeudet kuntoon
public interface KoulutustoimijaService {

    @PreAuthorize("permitAll()")
    KoulutustoimijaDto getKoulutustoimija(String kOid);

    @PreAuthorize("permitAll()")
    KoulutustoimijaDto getKoulutustoimija(Long kId);

    @PreAuthorize("permitAll()")
    List<KoulutustoimijaBaseDto> getKoulutustoimijat();

    @PreAuthorize("permitAll()")
    YhteinenDto getYhteinen(String kOid);

    @PreAuthorize("permitAll()")
    List<PoistettuDto> getYhteinenPoistetut(String kOid);

    @PreAuthorize("permitAll()")
    List<TyoryhmaDto> getTyoryhmat(String kOid);

    @PreAuthorize("permitAll()")
    YhteinenSisaltoDto getYhteinenSisalto(String kOid);

    @PreAuthorize("permitAll()")
    List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(String kOid);

    @PreAuthorize("permitAll()")
    OpetussuunnitelmaDto getOpetussuunnitelma(String kOid, Long opsId);

    @PreAuthorize("permitAll()")
    List<TiedoteDto> getTiedotteet(String kOid);

    @PreAuthorize("permitAll()")
    TiedoteDto getTiedote(String kOid);

    @PreAuthorize("permitAll()")
    List<TiedoteDto> getOmatTiedotteet(String kOid);
}

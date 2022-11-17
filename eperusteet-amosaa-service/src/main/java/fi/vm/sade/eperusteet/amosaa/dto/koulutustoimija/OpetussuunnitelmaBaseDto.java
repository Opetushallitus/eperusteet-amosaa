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

package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.JotpaTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

import lombok.*;

/**
 * @author nkala
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaBaseDto {

    private Long id;
    private LokalisoituTekstiDto nimi;
    private Tila tila;
    private OpsTyyppi tyyppi;
    private LokalisoituTekstiDto kuvaus;
    private KoulutustoimijaBaseDto koulutustoimija;
    private Date luotu;
    private Date muokattu;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Reference pohja;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long perusteId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String perusteDiaarinumero;

    private CachedPerusteBaseDto peruste;

    @Deprecated
    @ApiModelProperty(hidden = true)
    private Tila tila2016;

    private JotpaTyyppi jotpatyyppi;

}

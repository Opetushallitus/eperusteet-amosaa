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

package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author nkala
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutkinnonosaDto extends TutkinnonosaBaseDto {
    private OmaTutkinnonosaDto omatutkinnonosa;
    private VierasTutkinnonosaDto vierastutkinnonosa;
    private List<TutkinnonosaToteutusDto> toteutukset;
    private LokalisoituTekstiDto osaamisenOsoittaminen;
    private Long perusteentutkinnonosa;
    private List<VapaaTekstiDto> vapaat;
}

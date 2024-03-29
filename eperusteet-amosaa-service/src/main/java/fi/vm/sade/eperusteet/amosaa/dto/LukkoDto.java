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

package fi.vm.sade.eperusteet.amosaa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.Lukko;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Data
@EqualsAndHashCode
public class LukkoDto {

    public LukkoDto(Lukko lukko) {
        this(lukko, null);
    }

    public LukkoDto(Lukko lukko, Integer revisio) {
        this.haltijaOid = lukko.getHaltijaOid();
        this.haltijaNimi = "";
        this.luotu = lukko.getLuotu();
        this.vanhentuu = lukko.getVanhentuu();
        this.oma = lukko.isOma();
        this.revisio = revisio;
        this.vanhentunut = this.vanhentuu.isBefore(new Date().toInstant());
    }

    final String haltijaOid;

    @Setter
    String haltijaNimi;
    final Instant luotu;
    final Instant vanhentuu;
    final Boolean oma;
    final boolean vanhentunut;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer revisio;

    public static LukkoDto of(Lukko lukko) {
        return lukko == null ? null : new LukkoDto(lukko);
    }

    public static LukkoDto of(Lukko lukko, int revisio) {
        return lukko == null ? null : new LukkoDto(lukko, revisio);
    }
}

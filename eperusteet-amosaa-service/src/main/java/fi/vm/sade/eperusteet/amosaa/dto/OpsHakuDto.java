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

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import java.util.List;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpsHakuDto extends QueryDto {
    private Long koulutustoimija;
    private Long peruste;
    private List<KoulutusTyyppi> koulutustyyppi;
    private Set<Tila> tila;
    private Set<OpsTyyppi> tyyppi;
    private boolean organisaatioRyhma;

    public void setTila(Tila tila) {
        if (this.tila == null) {
            this.tila = new HashSet<>();
        }
        this.tila.add(tila);
    }

    public void setTila(Set<Tila> tila) {
        this.tila = tila;
    }

    public void setTyyppi(OpsTyyppi tyyppi) {
        if (this.tyyppi == null) {
            this.tyyppi = new HashSet<>();
        }
        this.tyyppi.add(tyyppi);
    }

    public void setTyyppi(Set<OpsTyyppi> tyyppi) {
        this.tyyppi = tyyppi;
    }
}

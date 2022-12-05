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

package fi.vm.sade.eperusteet.amosaa.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Set;
import java.util.UUID;

import lombok.*;

/**
 * @author nkala
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuorituspolkuRiviDto extends SuorituspolkuRiviJulkinenDto {
    private UUID rakennemoduuli;
    private Long jrno;
    private Boolean piilotettu;

    static public SuorituspolkuRiviDto of(UUID moduuli, Boolean piilotettu, LokalisoituTekstiDto kuvaus) {
        SuorituspolkuRiviDto result = new SuorituspolkuRiviDto();
        result.setRakennemoduuli(moduuli);
        result.setPiilotettu(piilotettu);
        result.setKuvaus(kuvaus);
        return result;
    }

    static public SuorituspolkuRiviDto of(String moduuli, Boolean piilotettu, LokalisoituTekstiDto kuvaus) {
        return of(UUID.fromString(moduuli), piilotettu, kuvaus);
    }
    
    static public SuorituspolkuRiviDto of(UUID moduuli, Boolean piilotettu, LokalisoituTekstiDto kuvaus, Set<String> koodit) {
        SuorituspolkuRiviDto suorituspolkuRiviDto = of(moduuli, piilotettu, kuvaus);
        suorituspolkuRiviDto.setKoodit(koodit);
        return suorituspolkuRiviDto;
    }

}

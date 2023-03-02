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
package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author harrik
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OsaAlueKokonaanDto extends OsaAlueDto {

    private Arviointi2020Dto arviointi;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Osaamistavoite2020Dto pakollisetOsaamistavoitteet;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Osaamistavoite2020Dto valinnaisetOsaamistavoitteet;

    private List<OsaamistavoiteLaajaDto> osaamistavoitteet;

    public LokalisoituTekstiDto getNimi() {
        if (getKoodi() != null && getKielikoodi() != null) {
            Map<Kieli, String> tekstit = new HashMap<>();
            Arrays.stream(Kieli.values()).forEach(kieli -> {
                if (getKoodi().getNimi().get(kieli.toString()) != null && getKielikoodi().getNimi().get(kieli.toString()) != null) {
                    tekstit.put(kieli, getKoodi().getNimi().get(kieli.toString()) + ", " + getKielikoodi().getNimi().get(kieli.toString()));
                }
            });
            return LokalisoituTekstiDto.of(tekstit);
        } else {
            return super.getNimi();
        }
    }
}

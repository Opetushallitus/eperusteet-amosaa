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

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.QueryDto;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nkala
 */
@Getter
@Setter
public class OpetussuunnitelmaJulkaistuQueryDto extends QueryDto {
    private String perusteenDiaarinumero = "";
    private Long perusteId = 0l;
    private String organisaatio = "";
    private List<String> tyyppi;
    private boolean organisaatioRyhma = false;
    private String oppilaitosTyyppiKoodiUri = "";
    private List<String> koulutustyyppi;

    private String nimi = "";
    private int sivukoko = 10;

    public List<String> getTyyppi() {
        if (tyyppi == null) {
            return Arrays.asList(Tyyppi.OPS.toString());
        }

        return tyyppi;
    }

}

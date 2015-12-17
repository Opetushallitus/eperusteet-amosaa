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

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author nkala
 */
@Getter
@Setter
public class OpetussuunnitelmaStatistiikkaDto implements Serializable {
    private Long id;
    private Set<Kieli> julkaisukielet;
    private Set<OrganisaatioDto> organisaatiot;
    private Set<KoodistoDto> kunnat;
    private LokalisoituTekstiDto kuvaus;
    private Date muokattu;
    private LokalisoituTekstiDto nimi;
    private String perusteenDiaarinumero;
    private Tila tila;
    private KoulutusTyyppi koulutustyyppi;
}

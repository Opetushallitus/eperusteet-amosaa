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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.*;

/**
 * @author jhyoty
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PerusteBaseDto implements Serializable {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private PerusteVersionDto globalVersion;
    private KoulutusTyyppi koulutustyyppi;
    private Set<Kieli> kielet;
    private LokalisoituTekstiDto kuvaus;
    private String diaarinumero;
    private Date voimassaoloAlkaa;
    private Date siirtymaPaattyy;
    private Date voimassaoloLoppuu;
    private Date muokattu;
    private String tila;
    private String tyyppi;
    private Set<String> korvattavatDiaarinumerot;
    private Set<KoulutusDto> koulutukset;
    private Set<KoodiDto> osaamisalat;
    private KoulutustyyppiToteutus toteutus;
    List<TutkintonimikeKoodiDto> tutkintonimikkeet;
}

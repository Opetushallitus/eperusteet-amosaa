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
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.service.audit.AuditLoggableDto;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author mikkom
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SisaltoViiteDto implements AuditLoggableDto {
    @Override
    public void auditLog(LogMessage.LogMessageBuilder msg) {
    }

    private Long id;
    private TekstiKappaleDto tekstiKappale;
    private boolean pakollinen;
    private boolean valmis;
    private boolean liikkumaton;
    private Reference owner;
    private Reference vanhempi;
    private SisaltoTyyppi tyyppi;
    private LokalisoituTekstiDto ohjeteksti;
    private LokalisoituTekstiDto perusteteksti;
    private TutkinnonosaDto tosa;
    private SuorituspolkuDto suorituspolku;
    private String kommentti;

    @JsonProperty("_tekstiKappale")
    private Reference tekstiKappaleRef;

    public SisaltoViiteDto() {
    }

    public SisaltoViiteDto(TekstiKappaleDto tekstiKappale) {
        this.tekstiKappale = tekstiKappale;
    }

    @Getter
    @Setter
    public static class Matala extends SisaltoViiteDto {
        private List<Reference> lapset;

        public Matala() {
        }

        public Matala(TekstiKappaleDto tekstiKappale) {
            super(tekstiKappale);
        }
    }

    @Getter
    @Setter
    public static class Puu extends SisaltoViiteDto {
        private List<Puu> lapset;
    }
}

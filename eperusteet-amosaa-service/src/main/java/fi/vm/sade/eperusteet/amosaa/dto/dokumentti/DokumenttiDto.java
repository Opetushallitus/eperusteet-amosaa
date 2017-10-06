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

package fi.vm.sade.eperusteet.amosaa.dto.dokumentti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiEdistyminen;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import fi.vm.sade.eperusteet.amosaa.service.audit.AuditLoggableDto;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;

/**
 * @author iSaul
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DokumenttiDto implements AuditLoggableDto {
    @Override
    public void auditLog(LogMessage.LogMessageBuilder msg) {
    }

    private Long id;
    private Long opsId;
    private String luoja;
    private Kieli kieli;
    private Date aloitusaika;
    private Date valmistumisaika;
    private DokumenttiTila tila = DokumenttiTila.EI_OLE;
    private DokumenttiEdistyminen edistyminen = DokumenttiEdistyminen.TUNTEMATON;
    private String virhekoodi = "";
    private byte[] kansikuva;
    private byte[] ylatunniste;
    private byte[] alatunniste;
    private boolean perusteenSisalto;
}

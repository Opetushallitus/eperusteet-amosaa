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

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;
import fi.vm.sade.eperusteet.amosaa.service.audit.AuditLoggableDto;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;

/**
 *
 * @author jhyoty
 */
@Getter
@Setter
public class OsaamistasoDto implements AuditLoggableDto {
    @Override
    public void auditLog(LogMessage.LogMessageBuilder msg) {
    }

    private Long id;
    private LokalisoituTekstiDto otsikko;
}

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

package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.Setter;
import fi.vm.sade.eperusteet.amosaa.service.audit.AuditLoggableDto;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;

/**
 * @author nkala
 */
@Getter
@Setter
public class KayttajaDto extends KayttajaBaseDto implements AuditLoggableDto {
    @Override
    public void auditLog(LogMessage.LogMessageBuilder msg) {
    }

    private String etunimet;
    private String kutsumanimi;
    private String sukunimi;
    private Date tiedotekuittaus;
    private Set<Reference> suosikit;
    
    public static KayttajaDto of(KayttoikeusKayttajaDto kayttooikeusKayttaja, Long koulutustoimijaId) {
        KayttajaDto kayttajaDto = new KayttajaDto();
        BeanUtils.copyProperties(kayttooikeusKayttaja, kayttajaDto);
        
        kayttajaDto.setKoulutustoimija(koulutustoimijaId);
        
        return kayttajaDto;
    }
}

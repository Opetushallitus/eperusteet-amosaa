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
package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author isaul
 */
@Entity
@Table(name = "kommentti")
public class Kommentti extends AbstractAuditedReferenceableEntity {
    @Getter
    @Setter
    @NotNull
    @Column(name = "tekstikappaleviite_id")
    private Long tekstikappaleviiteId;

    @Getter
    @Setter
    @NotNull
    @Column(name = "parent_id")
    private Long parentId;

    @Getter
    @Setter
    @NotNull
    private Boolean poistettu;

    @Getter
    @Setter
    @NotNull
    @Column(length = 1024)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @Size(max = 1024, message = "Kommentin maksimipituus on {max} merkkiä")
    private String sisalto;
}

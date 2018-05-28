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

package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * @author nkala
 */
@Entity
@Audited
@Table(name = "vierastutkinnonosa")
public class VierasTutkinnonosa extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Copyable<VierasTutkinnonosa> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @NotNull
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private CachedPeruste cperuste;

    @Getter
    @Setter
    @NotNull
    @Column(name = "peruste_id")
    private Long perusteId;

    @Getter
    @Setter
    @NotNull
    @Column(name = "tosa_id")
    private Long tosaId;

    @Override
    public VierasTutkinnonosa copy(boolean deep) {
        return copy(this);
    }

    public static VierasTutkinnonosa copy(VierasTutkinnonosa original) {
        if (original != null) {
            VierasTutkinnonosa result = new VierasTutkinnonosa();
            result.setCperuste(original.getCperuste());
            result.setPerusteId(original.getPerusteId());
            result.setTosaId(original.getTosaId());
            return result;
        }
        else {
            return null;
        }
    }
}

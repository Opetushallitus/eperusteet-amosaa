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

import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Table(name = "suorituspolku_rivi")
public class SuorituspolkuRivi implements Serializable, ReferenceableEntity, Copyable<SuorituspolkuRivi> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Suorituspolku suorituspolku;

    @Getter
    @Setter
    @NotNull
    private UUID rakennemoduuli;

    @Getter
    @Setter
    private Boolean piilotettu = false;

    @Getter
    @Setter
    private Long jrno;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti kuvaus;

    @Getter
    @Setter
    @ElementCollection
    private Set<String> koodit;

    @Override
    public SuorituspolkuRivi copy(boolean deep) {
        SuorituspolkuRivi result = new SuorituspolkuRivi();
        result.setRakennemoduuli(this.getRakennemoduuli());
        result.setPiilotettu(this.getPiilotettu());
        result.setJrno(this.getJrno());
        result.setKuvaus(this.getKuvaus());
        result.setKoodit(new HashSet<>(this.koodit));
        return result;
    }
}

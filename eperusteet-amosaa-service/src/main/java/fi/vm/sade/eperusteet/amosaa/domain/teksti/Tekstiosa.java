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

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * @author jhyoty
 */
@Audited
@Table(name = "tekstiosa")
@Entity
public class Tekstiosa implements Serializable, Copyable<Tekstiosa> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    private LokalisoituTeksti otsikko;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml
    private LokalisoituTeksti teksti;

    public Tekstiosa() {
    }

    public Tekstiosa(LokalisoituTeksti otsikko, LokalisoituTeksti teksti) {
        this.otsikko = otsikko;
        this.teksti = teksti;
    }

    public Tekstiosa(Tekstiosa other) {
        this.otsikko = other.getOtsikko();
        this.teksti = other.getTeksti();
    }

    public static Tekstiosa copyOf(Tekstiosa other) {
        if (other == null) return null;
        return new Tekstiosa(other);
    }

    public static void validoi(Validointi validointi, Tekstiosa osa, Opetussuunnitelma kielet, LokalisoituTeksti parent) {
        validoiOtsikko(validointi, osa, kielet, parent);
        validoiTeksti(validointi, osa, kielet, parent);
    }


    public static void validoiOtsikko(Validointi validointi, Tekstiosa osa, Opetussuunnitelma ops, LokalisoituTeksti parent) {
        LokalisoituTeksti.validoi(validointi, ops, osa.getOtsikko(), parent);
    }

    public static void validoiTeksti(Validointi validointi, Tekstiosa osa, Opetussuunnitelma ops, LokalisoituTeksti parent) {
        LokalisoituTeksti.validoi(validointi, ops, osa.getTeksti(), parent);
    }

    @Override
    public Tekstiosa copy(boolean deep) {
        return copyOf(this);
    }
}

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
package fi.vm.sade.eperusteet.amosaa.domain.arviointi;

import fi.vm.sade.eperusteet.amosaa.domain.OsaamistasonKriteeri;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidArvioinninKohde;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static fi.vm.sade.eperusteet.amosaa.service.util.Util.refXnor;


/**
 * @author teele1
 */
@Entity
@Table(name = "arvioinninkohde")
@ValidArvioinninKohde
@Audited
public class ArvioinninKohde implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti otsikko;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti selite;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Arviointiasteikko arviointiasteikko;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Getter
    @BatchSize(size = 10)
    private Set<OsaamistasonKriteeri> osaamistasonKriteerit = new HashSet<>();

    @Getter
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "arvioinninkohdealue_arvioinninkohde",
            inverseJoinColumns = @JoinColumn(name = "arvioinninkohdealue_id"),
            joinColumns = @JoinColumn(name = "arvioinninkohde_id"))
    private Set<ArvioinninKohdealue> arvioinninKohdealueet = new HashSet<>();

    public ArvioinninKohde() {
    }

    public ArvioinninKohde(ArvioinninKohde other) {
        this.otsikko = other.getOtsikko();
        this.arviointiasteikko = other.getArviointiasteikko();
        for (OsaamistasonKriteeri k : other.getOsaamistasonKriteerit()) {
            this.osaamistasonKriteerit.add(new OsaamistasonKriteeri(k));
        }
    }

    public void setOsaamistasonKriteerit(Set<OsaamistasonKriteeri> osaamistasonKriteerit) {
        this.osaamistasonKriteerit.clear();
        if (osaamistasonKriteerit != null) {
            this.osaamistasonKriteerit.addAll(osaamistasonKriteerit);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.otsikko);
        hash = 41 * hash + Objects.hashCode(this.arviointiasteikko);
        hash = 41 * hash + Objects.hashCode(this.osaamistasonKriteerit);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ArvioinninKohde) {
            final ArvioinninKohde other = (ArvioinninKohde) obj;
            if (!Objects.equals(this.otsikko, other.otsikko)) {
                return false;
            }
            if (!Objects.equals(this.selite, other.selite)) {
                return false;
            }
            if (!Objects.equals(this.arviointiasteikko, other.arviointiasteikko)) {
                return false;
            }
            return Objects.equals(this.osaamistasonKriteerit, other.osaamistasonKriteerit);
        }
        return false;
    }

    public boolean structureEquals(ArvioinninKohde other) {
        if (this == other) {
            return true;
        }
        boolean result = refXnor(getOtsikko(), other.getOtsikko());
        result &= Objects.equals(getArviointiasteikko(), other.getArviointiasteikko());
        result &= refXnor(getOsaamistasonKriteerit(), other.getOsaamistasonKriteerit());
        return result;
    }

}

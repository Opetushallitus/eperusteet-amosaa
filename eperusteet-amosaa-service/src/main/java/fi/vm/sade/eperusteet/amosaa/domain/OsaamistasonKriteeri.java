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
package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.arviointi.ArvioinninKohde;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author teele1
 */
@Entity
@Table(name = "osaamistasonkriteeri")
@Audited
public class OsaamistasonKriteeri implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Osaamistaso osaamistaso;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @OrderColumn
    @JoinTable(name = "osaamistasonkriteeri_tekstipalanen",
               joinColumns = @JoinColumn(name = "osaamistasonkriteeri_id"),
               inverseJoinColumns = @JoinColumn(name = "tekstipalanen_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @BatchSize(size = 25)
    private List<Tekstiosa> kriteerit = new ArrayList<>();

    @Getter
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "arvioinninkohde_osaamistasonkriteeri",
        joinColumns = @JoinColumn(name = "osaamistason_kriteerit_id", updatable = false, nullable = false),
        inverseJoinColumns = @JoinColumn(name = "arvioinninkohde_id", nullable = false, updatable = false))
    private Set<ArvioinninKohde> arvioinninKohteet = new HashSet<>();

    public OsaamistasonKriteeri() {
    }

    public OsaamistasonKriteeri(OsaamistasonKriteeri other) {
        this.osaamistaso = other.osaamistaso;
        this.kriteerit.addAll(other.kriteerit);
    }

    public List<Tekstiosa> getKriteerit() {
        return new ArrayList<>(kriteerit);
    }

    public void setKriteerit(List<Tekstiosa> kriteerit) {
        this.kriteerit.clear();
        if (kriteerit != null) {
            for (Tekstiosa t : kriteerit) {
                if (t != null) {
                    this.kriteerit.add(t);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.osaamistaso);
        hash = 29 * hash + Objects.hashCode(this.kriteerit);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OsaamistasonKriteeri) {
            final OsaamistasonKriteeri other = (OsaamistasonKriteeri) obj;
            if (!Objects.equals(this.osaamistaso, other.osaamistaso)) {
                return false;
            }
            return Objects.equals(this.kriteerit, other.kriteerit);
        }
        return false;
    }

}

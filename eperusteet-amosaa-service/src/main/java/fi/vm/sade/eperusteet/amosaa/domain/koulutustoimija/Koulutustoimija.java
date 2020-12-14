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

package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * @author nkala
 */
@Entity
@Audited
@Table(name = "koulutustoimija")
public class Koulutustoimija extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @Getter
    @Setter
    @ElementCollection
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Koulutustoimija> ystavat = new HashSet<>();

    @Getter
    @Setter
    private String organisaatio = "";

    @Getter
    @Setter
    private boolean salliystavat = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "koulutustoimija_liite", inverseJoinColumns = {
            @JoinColumn(name = "liite_id")
    }, joinColumns = {
            @JoinColumn(name = "koulutustoimija_id")
    })
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Set<Liite> liitteet = new HashSet<>();

    @ElementCollection
    @Getter
    @Setter
    private Set<String> organisaatioTyypit = new HashSet<>();

    public boolean isOph() {
        return SecurityUtil.OPH_OID.equals(this.organisaatio);
    }

    public void attachLiite(Liite liite) {
        liitteet.add(liite);
    }

    public void removeLiite(Liite liite) {
        liitteet.remove(liite);
    }

    public void addOrganisaatioTyyppi(String organisaatioTyyppi) {
        if (organisaatioTyypit == null) {
            organisaatioTyypit = new HashSet<>();
        }

        organisaatioTyypit.add(organisaatioTyyppi);
    }

    public boolean isOrganisaatioRyhma() {
        return organisaatioTyypit == null || organisaatioTyypit.contains("Ryhma");
    }
}

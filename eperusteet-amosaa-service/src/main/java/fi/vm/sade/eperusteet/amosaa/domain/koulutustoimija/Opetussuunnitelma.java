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
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 *
 * @author nkala
 */
@Entity
@Audited
@Table(name = "opetussuunnitelma")
public class Opetussuunnitelma extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    @NotNull
    private Tila tila = Tila.LUONNOS;

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    @NotNull
    private OpsTyyppi tyyppi = OpsTyyppi.OPS;

    @Getter
    @Setter
    private String perusteDiaarinumero;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private CachedPeruste peruste;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @NotNull
    private LokalisoituTeksti nimi;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti kuvaus;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    @NotNull
    private Koulutustoimija koulutustoimija;

    @ElementCollection
    @Getter
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    private Set<Kieli> julkaisukielet = new HashSet<>();

    @Getter
    @Setter
    private boolean esikatseltavissa = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date voimaantulo;

    @Getter
    @Setter
    private String hyvaksyja;

    @Getter
    @Setter
    private String paatosnumero;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date paatospaivamaara;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Opetussuunnitelma pohja;

    @ManyToMany(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinTable(name = "opetussuunnitelma_liite", inverseJoinColumns = {@JoinColumn(name="liite_id")}, joinColumns = {@JoinColumn(name="opetussuunnitelma_id")})
    @Getter
    @Setter
    private Set<Liite> liitteet = new HashSet<>();
}

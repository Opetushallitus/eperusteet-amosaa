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
package fi.vm.sade.eperusteet.amosaa.domain.ohje;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jhyoty
 */
@Entity
@Getter
@Setter
public class Ohje extends AbstractAuditedEntity implements ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml
    private String kysymys;

    @ValidHtml
    private String vastaus;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti lokalisoituKysymys;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti lokalisoituVastaus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ohje_koulutustoimija",
            joinColumns = @JoinColumn(name = "ohje_id"),
            inverseJoinColumns = @JoinColumn(name = "koulutustoimija_id"))
    private Set<Koulutustoimija> koulutustoimijat = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    @NotNull
    private KoulutustyyppiToteutus toteutus = KoulutustyyppiToteutus.AMMATILLINEN;
}

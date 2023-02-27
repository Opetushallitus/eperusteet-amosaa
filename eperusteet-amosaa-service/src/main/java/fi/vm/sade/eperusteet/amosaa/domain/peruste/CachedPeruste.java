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

package fi.vm.sade.eperusteet.amosaa.domain.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.KoulutuksetConverter;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author nkala
 */
@Getter
@Setter
@Entity
@Table(name = "peruste_cache")
public class CachedPeruste implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti nimi;

    private String diaarinumero;

    @Column(columnDefinition = "text")
    private String peruste;

    @Column(name = "peruste_id")
    private Long perusteId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Enumerated(EnumType.STRING)
    private KoulutusTyyppi koulutustyyppi;

    //TODO add koulutustyyppitoteutus

    @Deprecated
    @Basic
    @Column(columnDefinition = "text")
    @Convert(converter = KoulutuksetConverter.class)
    private Set<KoulutusDto> koulutukset;

    @OneToMany(mappedBy = "cachedPeruste", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Koulutuskoodi> koulutuskoodit = new HashSet<>();

    public void setKoulutuskoodit(Set<Koulutuskoodi> koulutuskoodit) {
        this.koulutuskoodit.clear();
        this.koulutuskoodit.addAll(koulutuskoodit);
    }

    public void setKoulutuskooditFromKoulutusDto(Set<KoulutusDto> koulutusDtos) {
        if (koulutusDtos != null) {
            setKoulutuskoodit(koulutusDtos.stream().map(koulutusDto -> Koulutuskoodi.of(koulutusDto, this)).collect(Collectors.toSet()));
        }
    }
}

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
package fi.vm.sade.eperusteet.amosaa.domain.liite;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jhyoty
 */
@Entity
@Setter
@Table(name = "liite")
public class Liite implements Serializable {

    @Id
    @Getter
    @Column(updatable = false)
    private UUID id;

    @Getter
    @NotNull
    @Basic(optional = false)
    private String tyyppi;

    @Getter
    @Size(max = 1024)
    private String nimi;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Getter
    @Basic(fetch = FetchType.LAZY, optional = false)
    @Lob
    @NotNull
    private Blob data;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "opetussuunnitelma_liite",
            joinColumns = @JoinColumn(name = "liite_id"),
            inverseJoinColumns = @JoinColumn(name = "opetussuunnitelma_id"))
    @Getter
    private Set<Opetussuunnitelma> opetussuunnitelmat;

    protected Liite() {
        //JPA
    }

    public Liite(String tyyppi, String nimi, Blob data) {
        this.id = UUID.randomUUID();
        this.luotu = new Date();
        this.nimi = nimi;
        this.tyyppi = tyyppi;
        this.data = data;
    }

    public Date getLuotu() {
        return new Date(luotu.getTime());
    }

}

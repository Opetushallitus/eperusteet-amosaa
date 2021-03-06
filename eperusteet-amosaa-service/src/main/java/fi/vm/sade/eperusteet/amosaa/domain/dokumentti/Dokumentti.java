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

package fi.vm.sade.eperusteet.amosaa.domain.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * @author iSaul
 */
@Entity
@Table(name = "dokumentti")
@Getter
@Setter
public class Dokumentti {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "opetussuunnitelma_id")
    private Long opsId;

    private String luoja;

    @Column(insertable = true, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Kieli kieli;

    @Temporal(TemporalType.TIMESTAMP)
    private Date aloitusaika;

    @Temporal(TemporalType.TIMESTAMP)
    private Date valmistumisaika;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DokumenttiTila tila = DokumenttiTila.EI_OLE;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DokumenttiEdistyminen edistyminen = DokumenttiEdistyminen.TUNTEMATON;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "dokumenttidata")
    private byte[] data;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] kansikuva;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] ylatunniste;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] alatunniste;

    @Column(name = "virhekoodi")
    private String virhekoodi;

    @Getter
    @Setter
    @Column(name = "perusteen_sisalto")
    private boolean perusteenSisalto = false;
}

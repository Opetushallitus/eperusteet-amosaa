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

package fi.vm.sade.eperusteet.amosaa.domain.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
//import fi.vm.sade.eperusteet.amosaa.domain.ops.Opetussuunnitelma;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author nkala
 */
@Entity
@Table(name = "kayttaja_oikeudet")
public class Kayttajaoikeus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Kayttaja kayttaja;

//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Getter
//    @Setter
//    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
//    private Opetussuunnitelma ops;

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Koulutustoimija koulutustoimija;

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    private KayttajaoikeusTyyppi oikeus;
}
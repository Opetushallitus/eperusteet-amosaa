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

package fi.vm.sade.eperusteet.amosaa.repository.kayttaja;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaKuittaus;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author nkala
 */
public interface KayttajaKuittausRepository extends JpaRepository<KayttajaKuittaus, Long> {
    @Query("SELECT k.tiedote FROM KayttajaKuittaus k WHERE k.kayttaja = ?1")
    Set<Long> findAllByKayttaja(Long kayttajaId);
}

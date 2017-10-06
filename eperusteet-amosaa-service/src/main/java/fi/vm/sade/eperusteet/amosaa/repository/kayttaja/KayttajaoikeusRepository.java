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

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author nkala
 */
@Repository
public interface KayttajaoikeusRepository extends JpaRepository<Kayttajaoikeus, Long> {
    @Query("SELECT o.oikeus FROM Kayttajaoikeus o WHERE o.kayttaja.id = ?1 AND o.koulutustoimija.id = ?2")
    List<KayttajaoikeusTyyppi> findKoulutustoimijaOikeus(Long kayttaja, Long koulutustoimija);

    @Query("SELECT o.oikeus FROM Kayttajaoikeus o WHERE o.kayttaja.id = ?1 AND o.koulutustoimija.id = ?2")
    KayttajaoikeusTyyppi findKayttajaoikeus(Long kayttaja, Long koulutustoimija);

    @Query("SELECT o.oikeus FROM Kayttajaoikeus o WHERE o.kayttaja.oid = ?1 AND o.opetussuunnitelma.id = ?2")
    KayttajaoikeusTyyppi findKayttajaoikeus(String kayttajaOid, Long opsId);

    List<Kayttajaoikeus> findAllByKoulutustoimijaAndOpetussuunnitelma(Koulutustoimija koulutustoimija, Opetussuunnitelma ops);

    @Query("SELECT o FROM Kayttajaoikeus o WHERE o.kayttaja.id = ?1 AND o.koulutustoimija.id = ?2")
    List<Kayttajaoikeus> findAllKayttajaoikeus(Long kayttaja, Long koulutustoimija);

    Kayttajaoikeus findOneByKayttajaAndKoulutustoimijaAndOpetussuunnitelma(Kayttaja a, Koulutustoimija b, Opetussuunnitelma c);

    List<Kayttajaoikeus> findAllByKayttajaAndKoulutustoimija(Kayttaja kayttaja, Koulutustoimija koulutustoimija);

    List<Kayttajaoikeus> findAllByKayttaja(Kayttaja kayttaja);

    Kayttajaoikeus findOneByKayttajaAndOpetussuunnitelma(Kayttaja kayttaja, Opetussuunnitelma ops);
}

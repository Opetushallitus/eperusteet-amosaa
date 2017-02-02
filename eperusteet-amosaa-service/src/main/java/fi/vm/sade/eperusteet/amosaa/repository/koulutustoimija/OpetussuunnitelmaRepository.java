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

package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author nkala
 */
@Repository
public interface OpetussuunnitelmaRepository extends JpaWithVersioningRepository<Opetussuunnitelma, Long> {
    @Query("SELECT o FROM Opetussuunnitelma o WHERE o.koulutustoimija = ?1 AND o.peruste.perusteId = ?2")
    List<Opetussuunnitelma> findAllByKoulutustoimijaAndPerusteId(Koulutustoimija koulutustoimija, Long perusteId);

    List<Opetussuunnitelma> findAllByKoulutustoimija(Koulutustoimija koulutustoimija);

    List<Opetussuunnitelma> findAllByKoulutustoimijaAndTila(Koulutustoimija koulutustoimija, Tila tila);

    List<Opetussuunnitelma> findAllByTyyppi(OpsTyyppi tyyppi);

    List<Opetussuunnitelma> findAllByTyyppiAndTila(OpsTyyppi tyyppi, Tila tila);

    @Query(value = "SELECT NEW fi.vm.sade.eperusteet.amosaa.service.util.Pair(o.tyyppi, o.tila) from Opetussuunnitelma o where o.id = ?1")
    Pair<Tyyppi,Tila> findTyyppiAndTila(long id);

    @Query(value = "SELECT NEW java.lang.Boolean(o.esikatseltavissa) from Opetussuunnitelma o where o.id = ?1")
    Boolean isEsikatseltavissa(long id);

    @Query(value = "SELECT o FROM Opetussuunnitelma o WHERE o.perusteDiaarinumero = ?1 AND (o.tila = fi.vm.sade.eperusteet.amosaa.domain.Tila.JULKAISTU or o.esikatseltavissa = true)")
    List<Opetussuunnitelma> findAllByPerusteDiaarinumero(String diaari);
}

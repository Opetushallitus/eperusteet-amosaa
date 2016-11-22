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
package fi.vm.sade.eperusteet.amosaa.repository.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mikkom
 */
@Repository
public interface SisaltoviiteRepository extends JpaWithVersioningRepository<SisaltoViite, Long> {
    List<SisaltoViite> findAllByTekstiKappale(TekstiKappale tekstiKappale);
    List<SisaltoViite> findAllByOwner(Opetussuunnitelma owner);
    List<SisaltoViite> findAllByOwnerId(Long owner);
    SisaltoViite findOneByOwnerAndId(Opetussuunnitelma owner, Long id);
    SisaltoViite findOneByOwnerIdAndId(Long owner, Long id);

    @Query(value = "SELECT sv from SisaltoViite sv where sv.owner = ?1 AND sv.vanhempi IS NULL")
    SisaltoViite findOneRoot(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.tosa.tyyppi = 'OMA' AND sv.owner.koulutustoimija = ?1")
    List<SisaltoViite> findAllPaikallisetTutkinnonOsat(Koulutustoimija kt);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.tosa.tyyppi = 'OMA' AND sv.owner.koulutustoimija = ?1 AND sv.tosa.omatutkinnonosa.koodi = ?2")
    List<SisaltoViite> findAllPaikallisetTutkinnonOsatByKoodi(Koulutustoimija kt, String koodi);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.tosa.tyyppi = 'PERUSTEESTA' AND sv.owner.koulutustoimija = ?1 AND sv.tosa.koodi = ?2")
    List<SisaltoViite> findAllTutkinnonOsatByKoodi(Koulutustoimija kt, String koodi);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'TUTKINNONOSAT'")
    SisaltoViite findTutkinnonosatRoot(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'SUORITUSPOLUT'")
    SisaltoViite findSuorituspolutRoot(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'TUTKINNONOSA'")
    List<SisaltoViite> findTutkinnonosat(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'SUORITUSPOLKU'")
    List<SisaltoViite> findSuorituspolut(Opetussuunnitelma owner);
}

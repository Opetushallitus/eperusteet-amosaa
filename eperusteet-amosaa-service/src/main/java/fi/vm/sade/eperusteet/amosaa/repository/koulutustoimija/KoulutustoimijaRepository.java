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

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author nkala
 */
@Repository
public interface KoulutustoimijaRepository extends JpaWithVersioningRepository<Koulutustoimija, Long>, KoulutustoimijaCustomRepository {
    Koulutustoimija findOneByOrganisaatio(final String organisaatio);

    @Query("SELECT kt.id FROM Koulutustoimija kt WHERE kt.organisaatio = ?1")
    Long findOneIdByOrganisaatio(final String organisaatio);

    default Koulutustoimija findOph() {
        return findOneByOrganisaatio(SecurityUtil.OPH_OID);
    }

    @Query("SELECT kt FROM Koulutustoimija kt WHERE kt.salliystavat = TRUE")
    List<Koulutustoimija> findAllYstavalliset();

    @Query("SELECT DISTINCT kt FROM Koulutustoimija kt JOIN kt.ystavat y WHERE ?1 = y")
    Set<Koulutustoimija> findAllYstavaPyynnotForKoulutustoimija(Koulutustoimija kt);

    List<Koulutustoimija> findByOrganisaatioIn(Collection<String> organisaatio);

    @Query(value = "SELECT DISTINCT kt " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.koulutustoimija kt " +
            "LEFT JOIN o.peruste p " +
            "WHERE p.koulutustyyppi IN (:koulutustyypit) OR o.koulutustyyppi IN (:koulutustyypit) " +
            "AND tila != 'POISTETTU' " +
            "AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')")
    List<Koulutustoimija> findByKoulutustyypit(@Param("koulutustyypit") Set<KoulutusTyyppi> koulutustyypit);
}

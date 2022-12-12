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
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author nkala
 */
@Repository
public interface OpetussuunnitelmaRepository extends JpaWithVersioningRepository<Opetussuunnitelma, Long>, OpetussuunnitelmaCustomRepository {
    @Query("SELECT o FROM Opetussuunnitelma o WHERE o.koulutustoimija = ?1 AND o.peruste.perusteId = ?2")
    Page<Opetussuunnitelma> findAllByKoulutustoimijaAndPerusteId(Koulutustoimija koulutustoimija, Long perusteId, Pageable pageable);

    @Query("SELECT o FROM Opetussuunnitelma o WHERE o.koulutustoimija = ?1 AND (o.peruste.koulutustyyppi = ?2 OR o.peruste IS NULL)")
    Page<Opetussuunnitelma> findAllByKoulutustoimijaAndKoulutustyyppiOrPerusteNull(Koulutustoimija koulutustoimija, KoulutusTyyppi koulutusTyyppi, Pageable pageable);

    Page<Opetussuunnitelma> findAllByKoulutustoimija(Koulutustoimija koulutustoimija, Pageable pageable);

    Page<Opetussuunnitelma> findAll(Pageable pageable);

    long countByKoulutustoimija(Koulutustoimija koulutustoimija);

    long countByKoulutustoimijaAndTila(Koulutustoimija koulutustoimija, Tila tila);

    long countByKoulutustoimijaInAndTila(List<Koulutustoimija> koulutustoimija, Tila tila);

    long countByPaatosnumeroAndIdNot(String paatosnumero, Long opsId);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt " +
            "WHERE kt IN (:koulutustoimijat) " +
            "AND o.tyyppi = :tyyppi " +
            "AND o.tila IN (:tilat)")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("tilat") Collection<Tila> tilat,
                       @Param("koulutustoimijat") Collection<Koulutustoimija> koulutustoimijat);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt LEFT JOIN o.peruste p " +
            "WHERE (COALESCE(:koulutustoimijat, null) IS NULL OR kt in (:koulutustoimijat)) " +
            "AND o.tyyppi = :tyyppi " +
            "AND o.tila IN (:tilat) " +
            "AND (p.koulutustyyppi IN (:koulutustyypit) or o.koulutustyyppi IN (:koulutustyypit))")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("tilat") Collection<Tila> tilat,
                       @Param("koulutustoimijat") Collection<Koulutustoimija> koulutustoimijat,
                       @Param("koulutustyypit") Collection<KoulutusTyyppi> koulutustyypit);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt " +
            "WHERE (COALESCE(:koulutustoimijat, null) IS NULL OR kt in (:koulutustoimijat)) " +
            "AND o.tyyppi = :tyyppi " +
            "AND ( (:julkaistu = false AND o.julkaisut IS EMPTY AND o.tila = 'LUONNOS') " +
            "       or (:julkaistu = true AND tila != 'POISTETTU' AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')) )")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("julkaistu") boolean julkaistu,
                       @Param("koulutustoimijat") Collection<Koulutustoimija> koulutustoimijat);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt LEFT JOIN o.peruste p " +
            "WHERE (COALESCE(:koulutustoimijat, null) IS NULL OR kt in (:koulutustoimijat)) " +
            "AND o.tyyppi = :tyyppi " +
            "AND ( (:julkaistu = false AND o.julkaisut IS EMPTY AND o.tila = 'LUONNOS') " +
            "       or (:julkaistu = true AND tila != 'POISTETTU' AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')) )" +
            "AND (p.koulutustyyppi IN (:koulutustyypit) or o.koulutustyyppi IN (:koulutustyypit))")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("julkaistu") boolean julkaistu,
                       @Param("koulutustoimijat") Collection<Koulutustoimija> koulutustoimijat,
                       @Param("koulutustyypit") Collection<KoulutusTyyppi> koulutustyypit);

    List<Opetussuunnitelma> findAllByKoulutustoimijaAndTila(Koulutustoimija koulutustoimija, Tila tila);

    List<Opetussuunnitelma> findAllByKoulutustoimijaAndTyyppi(Koulutustoimija koulutustoimija, OpsTyyppi tyyppi);

    List<Opetussuunnitelma> findAllByTyyppi(OpsTyyppi tyyppi);

    List<Opetussuunnitelma> findAllByTyyppiAndTila(OpsTyyppi tyyppi, Tila tila);

    @Query(value = "SELECT NEW fi.vm.sade.eperusteet.amosaa.service.util.Pair(o.tyyppi, o.tila) from Opetussuunnitelma o where o.id = ?1")
    Pair<Tyyppi, Tila> findTyyppiAndTila(long id);

    @Query(value = "SELECT NEW java.lang.Boolean(o.esikatseltavissa) from Opetussuunnitelma o where o.id = ?1")
    Boolean isEsikatseltavissa(long id);

    @Query(value = "SELECT o FROM Opetussuunnitelma o WHERE o.perusteDiaarinumero = ?1 AND (o.tila = fi.vm.sade.eperusteet.amosaa.domain.Tila.JULKAISTU or o.esikatseltavissa = true)")
    List<Opetussuunnitelma> findAllByPerusteDiaarinumero(String diaari);

    @Query(value = "SELECT o FROM Opetussuunnitelma o WHERE o.perusteDiaarinumero = ?1 AND (o.tila = fi.vm.sade.eperusteet.amosaa.domain.Tila.JULKAISTU)")
    List<Opetussuunnitelma> findAllJulkaistutByPerusteDiaarinumero(String diaari);

    List<Opetussuunnitelma> findByKoulutustoimijaOrganisaatio(String organisaatioId);

    List<Opetussuunnitelma> findAllByKoulutustoimijaId(Long ktId);

    List<Opetussuunnitelma> findAllByKoulutustoimijaIdAndTyyppi(Long ktId, OpsTyyppi tyyppi);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "LEFT JOIN o.nimi onimi " +
            "LEFT JOIN onimi.teksti nimi " +
            "JOIN o.koulutustoimija kt " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE (COALESCE(:koulutustoimijat, null) IS NULL OR kt.id in (:koulutustoimijat)) " +
            "AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) " +
            "AND nimi.kieli = :kieli " +
            "AND (:nimi IS NULL OR LOWER(nimi.teksti) LIKE LOWER(CONCAT('%', :nimi,'%'))) " +
            "AND (:jotpa = false OR o.jotpatyyppi IS NOT NULL) " +
            "AND ((:poistettu = false AND tila != 'POISTETTU' AND ((:julkaistu = false AND (o.julkaisut IS EMPTY AND o.tila = 'LUONNOS')) OR (:julkaistu = true AND (o.julkaisut IS NOT EMPTY OR o.tila = 'VALMIS')))) " +
            "    OR (:poistettu = true AND tila = 'POISTETTU')) " +
            "AND o.tyyppi = :tyyppi ")
    Page<Opetussuunnitelma> findByKoulutustoimijaInAndPerusteKoulutustyyppiInAndOpsTyyppi(
            @Param("koulutustoimijat") List<Long> ktId,
            @Param("koulutustyyppi") List<KoulutusTyyppi> koulutusTyyppi,
            @Param("nimi") String nimi,
            @Param("kieli") Kieli kieli,
            @Param("jotpa") Boolean jotpa,
            @Param("julkaistu") Boolean julkaistu,
            @Param("tyyppi") OpsTyyppi tyyppi,
            @Param("poistettu") Boolean poistettu,
            Pageable pageable);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.koulutustoimija kt " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE kt.id = :koulutustoimija AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi))")
    List<Opetussuunnitelma> findByKoulutustoimijaIdAndPerusteKoulutustyyppiIn(@Param("koulutustoimija") Long ktId, @Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.koulutustoimija kt " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE kt.id = :koulutustoimija AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) AND o.tyyppi = :tyyppi")
    List<Opetussuunnitelma> findByKoulutustoimijaIdAndPerusteKoulutustyyppiIn(@Param("koulutustoimija") Long ktId, @Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi, @Param("tyyppi") OpsTyyppi tyyppi);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.koulutustoimija kt " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE kt.id = :koulutustoimija AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) " +
            "AND o.tyyppi = :tyyppi AND o.tila IN(:tila)")
    List<Opetussuunnitelma> findByKoulutustoimijaIdAndTilaAndTyyppiPerusteKoulutustyyppiIn(
            @Param("koulutustoimija") Long ktId, @Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi, @Param("tyyppi") OpsTyyppi tyyppi, @Param("tila") Set<Tila> tila);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE o.tyyppi = :tyyppi " +
            "AND tila != 'POISTETTU' " +
            "AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) " +
            "AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')")
    List<Opetussuunnitelma> findJulkaistutByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi, @Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE o.tyyppi = :tyyppi " +
            "AND tila != 'POISTETTU' " +
            "AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')")
    List<Opetussuunnitelma> findJulkaistutByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi);

    @Query(value = "SELECT DISTINCT o FROM Opetussuunnitelma o " +
            "LEFT JOIN o.peruste p " +
            "WHERE (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) ")
    List<Opetussuunnitelma> findOpetussuunnitelmaTilastot(@Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi);

}

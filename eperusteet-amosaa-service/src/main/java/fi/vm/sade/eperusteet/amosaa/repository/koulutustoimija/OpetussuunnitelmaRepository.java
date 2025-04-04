package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.liite.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.amosaa.dto.ops.OpetussuunnitelmaWithLatestTilaUpdateTime;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OpetussuunnitelmaRepository extends JpaWithVersioningRepository<Opetussuunnitelma, Long>, OpetussuunnitelmaCustomRepository {
    @Query("SELECT o FROM Opetussuunnitelma o WHERE o.koulutustoimija = ?1 AND o.peruste.perusteId = ?2")
    Page<Opetussuunnitelma> findAllByKoulutustoimijaAndPerusteId(Koulutustoimija koulutustoimija, Long perusteId, Pageable pageable);

    @Query("SELECT o FROM Opetussuunnitelma o WHERE o.koulutustoimija = ?1 AND (o.peruste.koulutustyyppi = ?2 OR o.peruste IS NULL)")
    Page<Opetussuunnitelma> findAllByKoulutustoimijaAndKoulutustyyppiOrPerusteNull(Koulutustoimija koulutustoimija, KoulutusTyyppi koulutusTyyppi, Pageable pageable);

    Page<Opetussuunnitelma> findAllByKoulutustoimija(Koulutustoimija koulutustoimija, Pageable pageable);

    Page<Opetussuunnitelma> findAll(Pageable pageable);

    Page<Opetussuunnitelma> findAllByTyyppiIn(List<OpsTyyppi> opsTyyppi, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT ops.id, aud.muokattu AS viimeisinTilaMuutosAika " +
                    "FROM opetussuunnitelma ops " +
                    "INNER JOIN (SELECT MIN(muokattu) muokattu, tila, id FROM opetussuunnitelma_aud GROUP BY tila, id) aud ON aud.id = ops.id AND aud.tila = ops.tila " +
                    "WHERE ops.id in (:ids) AND ops.tyyppi in :tyyppi")
    List<OpetussuunnitelmaWithLatestTilaUpdateTime> findAllWithLatestTilaUpdateDate(List<Long> ids, List<String> tyyppi);

    long countByKoulutustoimija(Koulutustoimija koulutustoimija);

    long countByKoulutustoimijaAndTila(Koulutustoimija koulutustoimija, Tila tila);

    long countByKoulutustoimijaInAndTila(List<Koulutustoimija> koulutustoimija, Tila tila);

    long countByPaatosnumeroAndIdNot(String paatosnumero, Long opsId);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt " +
            "WHERE kt.id IN (:koulutustoimijat) " +
            "AND o.tyyppi = :tyyppi " +
            "AND o.tila IN (:tilat)")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("tilat") Collection<Tila> tilat,
                       @Param("koulutustoimijat") Collection<Long> koulutustoimijat);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt LEFT JOIN o.peruste p " +
            "WHERE (:koulutustoimijat IS NULL OR kt.id in (:koulutustoimijat)) " +
            "AND o.tyyppi = :tyyppi " +
            "AND o.tila IN (:tilat) " +
            "AND (p.koulutustyyppi IN (:koulutustyypit) or o.koulutustyyppi IN (:koulutustyypit))")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("tilat") Collection<Tila> tilat,
                       @Param("koulutustoimijat") Collection<Long> koulutustoimijat,
                       @Param("koulutustyypit") Collection<KoulutusTyyppi> koulutustyypit);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt " +
            "WHERE (:koulutustoimijat IS NULL OR kt.id in (:koulutustoimijat)) " +
            "AND o.tyyppi = :tyyppi " +
            "AND ( (:julkaistu = false AND o.julkaisut IS EMPTY AND o.tila = 'LUONNOS') " +
            "       or (:julkaistu = true AND tila != 'POISTETTU' AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')) )")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("julkaistu") boolean julkaistu,
                       @Param("koulutustoimijat") Collection<Long> koulutustoimijat);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.koulutustoimija kt LEFT JOIN o.peruste p " +
            "WHERE (:koulutustoimijat IS NULL OR kt.id in (:koulutustoimijat)) " +
            "AND o.tyyppi = :tyyppi " +
            "AND ( (:julkaistu = false AND o.julkaisut IS EMPTY AND o.tila = 'LUONNOS') " +
            "       or (:julkaistu = true AND tila != 'POISTETTU' AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')) )" +
            "AND (p.koulutustyyppi IN (:koulutustyypit) or o.koulutustyyppi IN (:koulutustyypit))")
    long countByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi,
                       @Param("julkaistu") boolean julkaistu,
                       @Param("koulutustoimijat") Collection<Long> koulutustoimijat,
                       @Param("koulutustyypit") Collection<KoulutusTyyppi> koulutustyypit);

    List<Opetussuunnitelma> findAllByKoulutustoimijaAndTila(Koulutustoimija koulutustoimija, Tila tila);

    @Query(value = "SELECT DISTINCT o " +
            "FROM Opetussuunnitelma o JOIN o.koulutustoimija kt LEFT JOIN o.peruste p " +
            "WHERE kt in (:koulutustoimijat) " +
            "AND o.tyyppi = :tyyppi " +
            "AND tila != 'POISTETTU'")
    List<Opetussuunnitelma> findAllByKoulutustoimijaAndTyyppi(Koulutustoimija koulutustoimijat, OpsTyyppi tyyppi);

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
            "WHERE (:koulutustoimijat IS NULL OR kt.id in (:koulutustoimijat)) " +
            "AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) " +
            "AND nimi.kieli = :kieli " +
            "AND (:nimi is null OR LOWER(nimi.teksti) LIKE LOWER(CONCAT('%', :nimi,'%'))) " +
            "AND (:jotpa = false OR o.jotpatyyppi IS NOT NULL) " +
            "AND ((:poistettu = false AND tila != 'POISTETTU' AND ((:julkaistuTaiValmis = false AND (o.julkaisut IS EMPTY AND o.tila = 'LUONNOS')) OR (:julkaistuTaiValmis = true AND (o.julkaisut IS NOT EMPTY OR o.tila = 'VALMIS')))) " +
            "    OR (:poistettu = true AND tila = 'POISTETTU')) " +
            "AND o.tyyppi = :tyyppi ")
    Page<Opetussuunnitelma> findByKoulutustoimijaInAndPerusteKoulutustyyppiInAndOpsTyyppi(
            @Param("koulutustoimijat") List<Long> ktId,
            @Param("koulutustyyppi") List<KoulutusTyyppi> koulutusTyyppi,
            @Param("nimi") String nimi,
            @Param("kieli") Kieli kieli,
            @Param("jotpa") Boolean jotpa,
            @Param("julkaistuTaiValmis") Boolean julkaistuTaiValmis,
            @Param("tyyppi") OpsTyyppi tyyppi,
            @Param("poistettu") Boolean poistettu,
            Pageable pageable);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.koulutustoimija kt " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE kt.id = :koulutustoimija AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) AND o.tila IN(:tila)")
    List<Opetussuunnitelma> findByKoulutustoimijaIdAndPerusteKoulutustyyppiIn(
            @Param("koulutustoimija") Long ktId,
            @Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi,
            @Param("tila") Set<Tila> tila);

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

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE o.tyyppi = :tyyppi " +
            "AND tila != 'POISTETTU' " +
            "AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi))")
    List<Opetussuunnitelma> findByTyyppiAndKoulutustyyppi(@Param("tyyppi") OpsTyyppi tyyppi, @Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE o.tyyppi = :tyyppi " +
            "AND tila != 'POISTETTU'")
    List<Opetussuunnitelma> findByTyyppi(@Param("tyyppi") OpsTyyppi tyyppi);

    @Query(value = "SELECT DISTINCT o FROM Opetussuunnitelma o " +
            "LEFT JOIN o.peruste p " +
            "WHERE (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi)) ")
    List<Opetussuunnitelma> findOpetussuunnitelmaTilastot(@Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi);

    @Query(value = "SELECT DISTINCT kt.oppilaitosTyyppiKoodiUri " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.koulutustoimija kt " +
            "LEFT OUTER JOIN o.peruste p " +
            "WHERE o.tila != 'POISTETTU' " +
            "AND kt.oppilaitosTyyppiKoodiUri IS NOT NULL " +
            "AND (p.koulutustyyppi IN (:koulutustyyppi) OR o.koulutustyyppi IN (:koulutustyyppi))")
    Set<String> findDistinctOppilaitosTyyppiKoodiUri(@Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi);
}

package fi.vm.sade.eperusteet.amosaa.repository.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.liite.version.JpaWithVersioningRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SisaltoviiteRepository extends JpaWithVersioningRepository<SisaltoViite, Long> {
    List<SisaltoViite> findAllByTekstiKappale(TekstiKappale tekstiKappale);

    List<SisaltoViite> findAllByOwner(Opetussuunnitelma owner);

    @Query(value = "SELECT DISTINCT sv " +
            "FROM SisaltoViite sv " +
            "LEFT JOIN FETCH sv.tekstiKappale tk " +
            "LEFT JOIN FETCH tk.nimi nimi " +
            "LEFT JOIN FETCH nimi.teksti tk " +
            "WHERE sv.owner.id = ?1")
    List<SisaltoViite> findAllByOwnerId(Long owner);

    SisaltoViite findOneByOwnerAndId(Opetussuunnitelma owner, Long id);

    SisaltoViite findOneByOwnerIdAndId(Long owner, Long id);

    @Query(value = "SELECT sv from SisaltoViite sv where sv.owner = ?1 AND sv.vanhempi IS NULL")
    SisaltoViite findOneRoot(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.tosa.tyyppi = 'OMA' AND sv.owner.koulutustoimija = ?1")
    List<SisaltoViite> findAllPaikallisetTutkinnonOsat(Koulutustoimija kt);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.tosa.tyyppi = 'OMA' AND sv.owner.koulutustoimija = ?1 AND sv.tosa.omatutkinnonosa.koodi = ?2")
    List<SisaltoViite> findAllPaikallisetTutkinnonOsatByKoodi(Koulutustoimija kt, String koodi);


    @Query(nativeQuery = true, value = "SELECT DISTINCT sv.* FROM SisaltoViite sv " +
            "INNER JOIN tutkinnonosa tosa ON tosa.id = sv.tosa_id " +
            "INNER JOIN opetussuunnitelma ops ON ops.id = sv.owner_id " +
            "LEFT OUTER JOIN vierastutkinnonosa vtosa ON vtosa.id = tosa.vierastutkinnonosa_id " +
            "LEFT OUTER JOIN tutkinnonosa vtosatosa ON vtosatosa.perusteentutkinnonosa = vtosa.tosa_id " +
            "WHERE 1 = 1 " +
            "AND (tosa.tyyppi = 'PERUSTEESTA' OR tosa.tyyppi ='VIERAS') " +
            "AND ops.koulutustoimija_id = ?1 " +
            "AND (tosa.koodi = ?2 or vtosatosa.koodi = ?2)")
    List<SisaltoViite> findAllTutkinnonOsatByKoodi(Koulutustoimija kt, String koodi);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'TUTKINNONOSAT'")
    SisaltoViite findTutkinnonosatRoot(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'SUORITUSPOLUT'")
    SisaltoViite findSuorituspolutRoot(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'TUTKINNONOSA'")
    List<SisaltoViite> findTutkinnonosat(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND sv.tyyppi = 'LINKKI'")
    List<SisaltoViite> findLinkit(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = :owner AND sv.tyyppi = :tyyppi")
    List<SisaltoViite> findByTyyppi(@Param("owner") Opetussuunnitelma owner, @Param("tyyppi") SisaltoTyyppi tyyppi);

    @Query(value = "SELECT sv FROM SisaltoViite sv WHERE sv.owner = ?1 AND (sv.tyyppi = 'SUORITUSPOLKU' OR sv.tyyppi = 'OSASUORITUSPOLKU')")
    List<SisaltoViite> findSuorituspolut(Opetussuunnitelma owner);

    @Query(value = "SELECT sv FROM SisaltoViite sv " +
            "LEFT JOIN sv.tekstiKappale tk " +
            "LEFT JOIN tk.nimi tkNimi " +
            "LEFT JOIN tkNimi.teksti nimi " +
            "WHERE sv.tyyppi = :tyyppi " +
            "AND sv.owner.koulutustoimija.id = :ktId " +
            "AND nimi.kieli = :kieli " +
            "AND (:opsTyyppi IS NULL OR sv.owner.tyyppi = :opsTyyppi) " +
            "AND (:nimi IS NULL OR LOWER(nimi.teksti) LIKE LOWER(CONCAT('%', :nimi,'%'))) " +
            "AND (:opsId IS NULL or sv.owner.id = :opsId) " +
            "AND (:notInOpetussuunnitelmaId IS NULL or sv.owner.id <> :notInOpetussuunnitelmaId) ")
    Page<SisaltoViite> findAllWithPagination(@Param("ktId") Long ktId,
                                             @Param("tyyppi") SisaltoTyyppi tyyppi,
                                             @Param("kieli") Kieli kieli,
                                             @Param("nimi") String nimi,
                                             @Param("opsId") Long opsId,
                                             @Param("opsTyyppi") OpsTyyppi opsTyyppi,
                                             @Param("notInOpetussuunnitelmaId") Long notInOpetussuunnitelmaId,
                                             Pageable pageable);

    @Query(value = "SELECT sv " +
            "FROM SisaltoViite sv " +
            "JOIN sv.tuvaLaajaAlainenOsaaminen lao " +
            "WHERE sv.owner = :owner " +
            "AND sv.tyyppi = 'LAAJAALAINENOSAAMINEN' " +
            "AND lao.nimiKoodi = :koodiUri")
    SisaltoViite findTuvaLaajaAlainenOsaaminenByKoodiUri(@Param("owner") Opetussuunnitelma owner, @Param("koodiUri") String koodiUri);

    @Query(value = "SELECT DISTINCT sv " +
            "FROM SisaltoViite sv " +
            "JOIN sv.tosa tosa " +
            "JOIN tosa.toteutukset tot " +
            "WHERE sv.owner.id = :opetussuunnitelmaId AND tot.oletustoteutus = true")
    List<SisaltoViite> findTutkinnonosienOletusotetutukset(@Param("opetussuunnitelmaId") Long opetussuunnitelmaId);
}

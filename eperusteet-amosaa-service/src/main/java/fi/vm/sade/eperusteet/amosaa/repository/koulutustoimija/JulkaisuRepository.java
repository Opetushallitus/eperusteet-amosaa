package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JulkaisuRepository extends JpaRepository<Julkaisu, Long> {

    List<Julkaisu> findAllByOpetussuunnitelma(Opetussuunnitelma opetussuunnitelma);

    boolean existsByOpetussuunnitelmaId(Long opetussuunnitelmaId);

    Julkaisu findFirstByOpetussuunnitelmaOrderByRevisionDesc(Opetussuunnitelma opetussuunnitelma);

    Julkaisu findByOpetussuunnitelmaAndRevision(Opetussuunnitelma opetussuunnitelma, int revision);

    String julkaisutQuery = "FROM ( " +
            "   SELECT * " +
            "   FROM julkaistu_opetussuunnitelma_Data_view data" +
            "   WHERE 1 = 1 " +
            "   AND (COALESCE(:koulutustyypit, NULL) = '' OR koulutustyyppi IN (:koulutustyypit) OR peruste->>'koulutustyyppi' IN (:koulutustyypit))" +
            "   AND (:nimi LIKE '' OR LOWER(nimi->>:kieli) LIKE LOWER(CONCAT('%',:nimi,'%'))) " +
            "   AND (:organisaatioRyhma = false OR cast(koulutustoimija->>'organisaatioRyhma' as boolean) = true) " +
            "   AND (:oppilaitosTyyppiKoodiUri = '' OR :oppilaitosTyyppiKoodiUri = data.\"oppilaitosTyyppiKoodiUri\")" +
            "   AND tyyppi IN (:tyyppi) " +
            "   AND (:organisaatio = '' OR koulutustoimija->>'organisaatio' = :organisaatio)" +
            "   AND ((:perusteId = 0) OR (:perusteId IS NULL) " +
            "           OR (cast(peruste->>'perusteId' as bigint) = :perusteId))" +
            "   AND ((:perusteenDiaarinumero IS NULL) OR (:perusteenDiaarinumero = '') " +
            "           OR (peruste->>'diaarinumero' = :perusteenDiaarinumero))" +
            "   AND ((:tulevat = true " +
            "                       AND (data.\"voimaantulo\" IS NOT NULL AND CAST(data.\"voimaantulo\" as bigint) > :nykyhetki)) " +
            "       OR (:poistuneet = true AND CAST(data.\"voimassaoloLoppuu\" as bigint) < :nykyhetki)" +
            "       OR (:voimassa = true " +
            "                       AND (data.\"voimaantulo\" IS NULL OR CAST(data.\"voimaantulo\" as bigint) < :nykyhetki) " +
            "                           AND (data.\"voimassaoloLoppuu\" IS NULL OR CAST(data.\"voimassaoloLoppuu\" as bigint) > :nykyhetki))) " +
            "   order by nimi->>:kieli asc, ?#{#pageable} " +
            ") t";

    @Query(nativeQuery = true,
            value = "SELECT CAST(row_to_json(t) as text) " + julkaisutQuery,
            countQuery = "SELECT count(*) " + julkaisutQuery
    )
    Page<String> findAllJulkisetJulkaisut(
            @Param("koulutustyypit") List<String> koulutustyypit,
            @Param("nimi") String nimi,
            @Param("kieli") String kieli,
            @Param("oppilaitosTyyppiKoodiUri") String oppilaitosTyyppiKoodiUri,
            @Param("organisaatioRyhma") boolean organisaatioRyhma,
            @Param("tyyppi") List<String> tyyppi,
            @Param("organisaatio") String organisaatio,
            @Param("perusteId") Long perusteId,
            @Param("perusteenDiaarinumero") String perusteenDiaarinumero,
            @Param("tulevat") boolean tulevat,
            @Param("voimassa") boolean voimassa,
            @Param("poistuneet") boolean poistuneet,
            @Param("nykyhetki") Long nykyhetki,
            Pageable pageable
    );

    @Query(nativeQuery = true, value =
            "SELECT CAST(row_to_json(t) as text) FROM ( " +
                    "   SELECT * " +
                    "   FROM julkaistu_opetussuunnitelma_Data_view data " +
                    "   WHERE EXISTS (SELECT 1 FROM JSONB_ARRAY_ELEMENTS(opintokokonaisuudet) elem WHERE elem->'opintokokonaisuus'->>'koodiArvo' LIKE :koodiarvo) " +
                    ") t")
    String findByOpintokokonaisuusKoodiArvo(@Param("koodiarvo") String koodiArvo);
}

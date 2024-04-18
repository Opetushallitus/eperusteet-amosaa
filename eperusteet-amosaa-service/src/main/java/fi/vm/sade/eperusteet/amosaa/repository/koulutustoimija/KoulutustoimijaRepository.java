package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.liite.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

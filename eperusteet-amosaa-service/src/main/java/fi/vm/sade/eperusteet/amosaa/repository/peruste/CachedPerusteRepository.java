package fi.vm.sade.eperusteet.amosaa.repository.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedPerusteRepository extends JpaRepository<CachedPeruste, Long> {
    List<CachedPeruste> findAllByDiaarinumero(String diaarinumero);

    CachedPeruste findFirstByDiaarinumeroOrderByLuotu(String diaarinumero);

    CachedPeruste findOneByDiaarinumeroAndLuotu(String diaarinumero, Date luotu);

    CachedPeruste findOneByPerusteIdAndLuotu(Long perusteId, Date luotu);

    CachedPeruste findFirstByPerusteIdAndLuotu(Long perusteId, Date luotu);

    CachedPeruste findFirstByPerusteIdOrderByLuotuDesc(Long perusteId);

    @Query("SELECT p.id " +
            "FROM CachedPeruste p " +
            "WHERE p.koulutukset IS NOT NULL " +
            "AND p.koulutuskoodit IS EMPTY")
    List<Long> findByKoulutuksetNotNull();

}

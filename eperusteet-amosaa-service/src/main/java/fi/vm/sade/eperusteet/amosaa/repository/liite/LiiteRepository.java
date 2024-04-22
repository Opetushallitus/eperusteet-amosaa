package fi.vm.sade.eperusteet.amosaa.repository.liite;

import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;

import java.util.List;
import java.util.UUID;

import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LiiteRepository extends CustomJpaRepository<Liite, UUID>, LiiteRepositoryCustom {
//    @Query("SELECT l FROM Koulutustoimija kt JOIN kt.liitteet l WHERE kt.id = ?1")
//    List<Liite> findByKoulutustoimijaId(Long koulutustoimijaId);

    @Query("SELECT l FROM Opetussuunnitelma ops JOIN ops.liitteet l WHERE ops.id = ?1")
    List<Liite> findByOpetussuunnitelmaId(Long opsId);

    @Query("SELECT l FROM Opetussuunnitelma ops JOIN ops.liitteet l WHERE ops.id = ?1 AND l.id = ?2")
    Liite findOne(Long opsId, UUID id);
}

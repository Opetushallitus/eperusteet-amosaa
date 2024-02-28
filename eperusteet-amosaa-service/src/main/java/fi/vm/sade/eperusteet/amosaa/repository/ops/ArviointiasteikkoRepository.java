package fi.vm.sade.eperusteet.amosaa.repository.ops;

import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointiasteikko;
import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author isaul
 */
@Repository
public interface ArviointiasteikkoRepository extends CustomJpaRepository<Arviointiasteikko, Long> {

}

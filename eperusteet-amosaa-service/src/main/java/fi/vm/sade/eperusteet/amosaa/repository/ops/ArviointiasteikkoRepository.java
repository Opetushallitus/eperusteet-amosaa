package fi.vm.sade.eperusteet.amosaa.repository.ops;

import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointiasteikko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArviointiasteikkoRepository extends JpaRepository<Arviointiasteikko, Long> {

}

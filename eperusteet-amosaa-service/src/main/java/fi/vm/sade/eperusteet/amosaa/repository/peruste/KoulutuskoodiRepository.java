package fi.vm.sade.eperusteet.amosaa.repository.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.peruste.Koulutuskoodi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoulutuskoodiRepository extends JpaRepository<Koulutuskoodi, Long> {

}

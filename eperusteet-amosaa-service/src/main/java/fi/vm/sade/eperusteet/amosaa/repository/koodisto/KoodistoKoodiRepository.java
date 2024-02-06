package fi.vm.sade.eperusteet.amosaa.repository.koodisto;

import fi.vm.sade.eperusteet.amosaa.domain.koodisto.KoodistoKoodi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KoodistoKoodiRepository extends JpaRepository<KoodistoKoodi, Long> {
    Optional<KoodistoKoodi> findByKoodiUri(String koodiUri);
}

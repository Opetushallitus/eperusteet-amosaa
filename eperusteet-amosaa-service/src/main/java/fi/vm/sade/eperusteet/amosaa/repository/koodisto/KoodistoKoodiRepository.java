package fi.vm.sade.eperusteet.amosaa.repository.koodisto;

import fi.vm.sade.eperusteet.amosaa.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KoodistoKoodiRepository extends CustomJpaRepository<KoodistoKoodi, Long> {
    Optional<KoodistoKoodi> findByKoodiUri(String koodiUri);
}

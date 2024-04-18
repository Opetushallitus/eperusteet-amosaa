package fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.liite.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutkinnonosaRepository extends JpaWithVersioningRepository<Tutkinnonosa, Long> {

}

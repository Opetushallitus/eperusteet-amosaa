package fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.VierasTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VierastutkinnonosaRepository extends JpaWithVersioningRepository<VierasTutkinnonosa, Long> {

}

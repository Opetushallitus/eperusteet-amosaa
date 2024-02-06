package fi.vm.sade.eperusteet.amosaa.repository.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OmaTutkinnonosaRepository extends JpaWithVersioningRepository<OmaTutkinnonosa, Long> {
}

package fi.vm.sade.eperusteet.amosaa.repository.teksti;


import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlue;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OmaOsaAlueRepository extends JpaWithVersioningRepository<OmaOsaAlue, Long> {
}

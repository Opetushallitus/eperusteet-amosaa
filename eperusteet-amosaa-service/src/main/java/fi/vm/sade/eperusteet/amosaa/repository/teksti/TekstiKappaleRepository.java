package fi.vm.sade.eperusteet.amosaa.repository.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.liite.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TekstiKappaleRepository extends JpaWithVersioningRepository<TekstiKappale, Long> {
}

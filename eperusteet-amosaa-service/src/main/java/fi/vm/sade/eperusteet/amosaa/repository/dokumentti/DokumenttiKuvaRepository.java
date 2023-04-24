package fi.vm.sade.eperusteet.amosaa.repository.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiKuva;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumenttiKuvaRepository extends JpaRepository<DokumenttiKuva, Long> {
    DokumenttiKuva findFirstByOpsIdAndKieli(Long opsId, Kieli kieli);
}

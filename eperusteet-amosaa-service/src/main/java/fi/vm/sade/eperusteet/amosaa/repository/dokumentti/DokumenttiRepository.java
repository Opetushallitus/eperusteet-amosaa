package fi.vm.sade.eperusteet.amosaa.repository.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;

import java.util.List;
import java.util.Set;

import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumenttiRepository extends CustomJpaRepository<Dokumentti, Long> {
    @Query("SELECT doc from Dokumentti doc WHERE doc.opsId = ?1 AND doc.kieli = ?2 AND doc.valmistumisaika IS NOT NULL ORDER BY doc.valmistumisaika DESC")
    List<Dokumentti> findByOpsIdAndKieliAndValmistumisaikaIsNotNull(Long opsId, Kieli kieli);

    Dokumentti findFirstByOpsIdAndKieliAndAloitusaikaNotNullOrderByAloitusaikaDesc(Long opsId, Kieli kieli);

    Dokumentti findByIdInAndKieli(Set<Long> id, Kieli kieli);
}

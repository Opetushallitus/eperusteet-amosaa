package fi.vm.sade.eperusteet.amosaa.repository.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumenttiRepository extends JpaRepository<Dokumentti, Long> {
    @Query("SELECT doc from Dokumentti doc WHERE doc.opsId = ?1 AND doc.kieli = ?2 ORDER BY doc.valmistumisaika")
    List<Dokumentti> findByOpsIdAndKieli(Long opsId, Kieli kieli);

    Dokumentti findById(Long id);
}

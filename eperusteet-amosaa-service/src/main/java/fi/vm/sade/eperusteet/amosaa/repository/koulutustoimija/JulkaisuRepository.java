package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JulkaisuRepository extends JpaRepository<Julkaisu, Long> {

    List<Julkaisu> findAllByOpetussuunnitelma(Opetussuunnitelma opetussuunnitelma);

    Julkaisu findFirstByOpetussuunnitelmaOrderByRevisionDesc(Opetussuunnitelma opetussuunnitelma);
}

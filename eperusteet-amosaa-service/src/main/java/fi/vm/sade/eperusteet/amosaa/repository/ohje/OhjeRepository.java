package fi.vm.sade.eperusteet.amosaa.repository.ohje;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.ohje.Ohje;
import java.util.List;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OhjeRepository extends JpaRepository<Ohje, Long> {

    List<Ohje> findByToteutus(KoulutustyyppiToteutus toteutus);
}

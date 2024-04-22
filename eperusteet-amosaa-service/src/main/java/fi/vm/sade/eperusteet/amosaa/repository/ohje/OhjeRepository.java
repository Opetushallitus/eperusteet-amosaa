package fi.vm.sade.eperusteet.amosaa.repository.ohje;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.ohje.Ohje;
import java.util.List;

import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OhjeRepository extends CustomJpaRepository<Ohje, Long> {

    List<Ohje> findByToteutus(KoulutustyyppiToteutus toteutus);
}

package fi.vm.sade.eperusteet.amosaa.repository.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LokalisoituTekstiRepository extends CustomJpaRepository<LokalisoituTeksti, Long> {
}

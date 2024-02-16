package fi.vm.sade.eperusteet.amosaa.repository.ops;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuorituspolkuRiviRepository extends JpaRepository<SuorituspolkuRivi, Long> {
    Set<SuorituspolkuRivi> findAllBySuorituspolku(Suorituspolku sp);

    void deleteBySuorituspolku(Suorituspolku sp);
}

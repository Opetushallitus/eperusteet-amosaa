package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Poistettu;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoistettuRepository extends JpaRepository<Poistettu, Long> {
    List<Poistettu> findAllByOpetussuunnitelma(Opetussuunnitelma ops);

    List<Poistettu> findAllByOpetussuunnitelmaAndTyyppi(Opetussuunnitelma ops, SisaltoTyyppi tyyppi);

    Poistettu findByOpetussuunnitelmaAndId(Opetussuunnitelma ops, Long id);
}

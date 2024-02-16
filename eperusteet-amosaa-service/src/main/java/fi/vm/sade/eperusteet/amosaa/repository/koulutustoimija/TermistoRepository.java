package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import java.util.List;

import fi.vm.sade.eperusteet.amosaa.domain.Termi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermistoRepository extends JpaWithVersioningRepository<Termi, Long> {
    List<Termi> findAllByKoulutustoimija(Koulutustoimija koulutustoimija);

    Termi findOneByKoulutustoimijaAndId(Koulutustoimija koulutustoimija, Long id);

    Termi findOneByKoulutustoimijaAndAvain(Koulutustoimija koulutustoimija, String avain);
}

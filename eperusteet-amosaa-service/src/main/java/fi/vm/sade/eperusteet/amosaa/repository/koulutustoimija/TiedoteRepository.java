package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Tiedote;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TiedoteRepository extends JpaRepository<Tiedote, Long> {
    List<Tiedote> findAllByKoulutustoimija(Koulutustoimija koulutustoimija);

    List<Tiedote> findAllByKoulutustoimijaAndJulkinenTrue(Koulutustoimija koulutustoimija);

    Tiedote findOneByKoulutustoimijaAndId(Koulutustoimija koulutustoimija, Long id);
}

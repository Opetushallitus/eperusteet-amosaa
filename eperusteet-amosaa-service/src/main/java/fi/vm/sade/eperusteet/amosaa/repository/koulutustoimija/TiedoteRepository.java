package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Tiedote;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;

import java.util.List;

import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;

public interface TiedoteRepository extends CustomJpaRepository<Tiedote, Long> {
    List<Tiedote> findAllByKoulutustoimija(Koulutustoimija koulutustoimija);

    List<Tiedote> findAllByKoulutustoimijaAndJulkinenTrue(Koulutustoimija koulutustoimija);

    Tiedote findOneByKoulutustoimijaAndId(Koulutustoimija koulutustoimija, Long id);
}

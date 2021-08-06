package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Tiedote;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.repository.CustomJpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by richard.vancamp on 29/03/16.
 */
public interface TiedoteRepository extends CustomJpaRepository<Tiedote, Long> {
    List<Tiedote> findAllByKoulutustoimija(Koulutustoimija koulutustoimija);

    List<Tiedote> findAllByKoulutustoimijaAndJulkinenTrue(Koulutustoimija koulutustoimija);

    Tiedote findOneByKoulutustoimijaAndId(Koulutustoimija koulutustoimija, Long id);
}

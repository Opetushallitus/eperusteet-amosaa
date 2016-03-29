package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.Tiedote;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.repository.version.JpaWithVersioningRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by richard.vancamp on 29/03/16.
 */
public interface TiedoteRepository extends JpaWithVersioningRepository<Tiedote, Long> {
    List<Tiedote> findAll(Koulutustoimija koulutustoimija);
    Tiedote findOneByKoulutustoimijaAndId(Koulutustoimija koulutustoimija, Long id);
}
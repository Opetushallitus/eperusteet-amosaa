package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaMuokkaustietoDto;
import java.util.Date;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpetussuunnitelmaMuokkaustietoService {

    @PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'LUKU')")
    List<OpetussuunnitelmaMuokkaustietoDto> getOpetussuunnitelmanMuokkaustiedot(Long ktId, Long opsId, Date viimeisinLuontiaika, int lukumaara);

    void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma);

    void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, String lisatieto);

    void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType);

    void addOpsMuokkausTieto(Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType, String lisatieto);
}

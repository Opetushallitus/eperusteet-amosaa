package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmaMuokkaustietoDto {

    private Long id;
    private LokalisoituTekstiDto nimi;
    private MuokkausTapahtuma tapahtuma;
    private Long perusteId;
    private Long kohdeId;
    private NavigationType kohde;
    private Date luotu;
    private String muokkaaja;
    private String lisatieto;
    private boolean poistettu;

    private KayttajanTietoDto kayttajanTieto;
}

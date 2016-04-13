package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by autio on 19.10.2015.
 */
@Getter
@Setter
public class AmmattitaitovaatimusKohdealueetDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
//    private List<AmmattitaitovaatimusKohdeDto> vaatimuksenKohteet;
}

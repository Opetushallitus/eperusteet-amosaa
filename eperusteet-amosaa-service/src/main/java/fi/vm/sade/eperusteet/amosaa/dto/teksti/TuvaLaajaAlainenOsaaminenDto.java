package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import lombok.Data;

@Data
public class TuvaLaajaAlainenOsaaminenDto implements KooditettuDto {

    private String nimiKoodi;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto teksti;
    private Boolean liite;

    @Override
    public void setKooditettu(LokalisoituTekstiDto kooditettu) {
        this.nimi = kooditettu;
    }
}

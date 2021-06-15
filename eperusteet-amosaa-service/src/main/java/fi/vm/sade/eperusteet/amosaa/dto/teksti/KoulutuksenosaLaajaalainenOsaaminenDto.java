package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import lombok.Data;

@Data
public class KoulutuksenosaLaajaalainenOsaaminenDto implements KooditettuDto {

    private Long id;
    private String koodiUri;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto laajaAlaisenOsaamisenKuvaus;

    @Override
    public void setKooditettu(LokalisoituTekstiDto kooditettu) {
        this.nimi = kooditettu;
    }
}

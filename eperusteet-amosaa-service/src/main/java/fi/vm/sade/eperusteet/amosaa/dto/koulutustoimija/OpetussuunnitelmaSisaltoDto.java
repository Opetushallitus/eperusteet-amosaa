package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import lombok.Data;

@Data
public class OpetussuunnitelmaSisaltoDto {
    // TODO: Tämä on kriittinen tehokkuuden kannalta. Tee nopeammaksi
    SisaltoViiteDto sisalto;
}

package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoodiDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaikallinenAmmattitaitovaatimus2019Dto {
    private KoodiDto koodi;
    private LokalisoituTekstiDto vaatimus;
}

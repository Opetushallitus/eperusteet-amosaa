package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.utils.dto.utils.LokalisoituTekstiDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ammattitaitovaatimus2019Dto {
    private KoodiDto koodi;
    private LokalisoituTekstiDto vaatimus;

    public LokalisoituTekstiDto getVaatimus() {
        if (this.koodi != null) {
            return new LokalisoituTekstiDto(this.koodi.getNimi());
        }
        return vaatimus;
    }
}
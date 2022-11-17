package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmmattitaitovaatimustenKohdealue2019Dto {
    private LokalisoituTekstiDto kuvaus;
    private List<Ammattitaitovaatimus2019Dto> vaatimukset = new ArrayList<>();
}

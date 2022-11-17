package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaikallinenAmmattitaitovaatimustenKohdealue2019Dto {
    private LokalisoituTekstiDto kuvaus;
    private List<PaikallinenAmmattitaitovaatimus2019Dto> vaatimukset = new ArrayList<>();
}

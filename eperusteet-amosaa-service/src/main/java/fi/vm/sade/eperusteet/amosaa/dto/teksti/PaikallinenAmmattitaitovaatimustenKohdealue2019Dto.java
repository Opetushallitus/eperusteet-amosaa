package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaikallinenAmmattitaitovaatimustenKohdealue2019Dto {
    private LokalisoituTekstiDto kuvaus;
    private List<PaikallinenAmmattitaitovaatimus2019Dto> vaatimukset = new ArrayList<>();
}

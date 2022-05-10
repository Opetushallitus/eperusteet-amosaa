package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.utils.dto.utils.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AmmattitaitovaatimustenKohdealue2019Dto {
    private LokalisoituTekstiDto kuvaus;
    private List<Ammattitaitovaatimus2019Dto> vaatimukset = new ArrayList<>();
}
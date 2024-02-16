package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TekstiosaDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto teksti;

    public TekstiosaDto(LokalisoituTekstiDto otsikko, LokalisoituTekstiDto teksti) {
        this.otsikko = otsikko;
        this.teksti = teksti;
    }

    public static TekstiosaDto of(String teksti) {
        TekstiosaDto tekstiosaDto = new TekstiosaDto();
        tekstiosaDto.setTeksti(LokalisoituTekstiDto.of(teksti));

        return tekstiosaDto;
    }
}

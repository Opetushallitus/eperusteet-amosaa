package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpintokokonaisuusTavoiteDto implements KooditettuDto {
    private Long id;
    private Boolean perusteesta;
    private String tavoiteKoodi;
    private KoodistoKoodiDto koodi;
    private LokalisoituTekstiDto tavoite;

    @Override
    public void setKoodistoKoodi(KoodistoKoodiDto koodi) {
        this.koodi = koodi;
    }
}

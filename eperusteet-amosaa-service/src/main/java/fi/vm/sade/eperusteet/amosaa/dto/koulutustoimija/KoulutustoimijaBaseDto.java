package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KoulutustoimijaBaseDto {
    private Long id;
    private String organisaatio;
    private LokalisoituTekstiDto nimi;
    private boolean organisaatioRyhma;
    private LokalisoituTekstiDto oppilaitostyyppi;
    private String oppilaitosTyyppiKoodiUri;
}

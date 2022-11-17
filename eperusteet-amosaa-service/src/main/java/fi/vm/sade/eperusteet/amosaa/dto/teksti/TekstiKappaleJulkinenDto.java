package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TekstiKappaleJulkinenDto {
    private Long id;
    private Date luotu;
    private Date muokattu;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto teksti;
    private UUID tunniste;
}

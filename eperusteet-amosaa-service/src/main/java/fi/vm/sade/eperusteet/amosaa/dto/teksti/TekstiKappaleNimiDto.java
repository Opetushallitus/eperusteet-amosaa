package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TekstiKappaleNimiDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
}

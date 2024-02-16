package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VapaaTekstiDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto teksti;
}

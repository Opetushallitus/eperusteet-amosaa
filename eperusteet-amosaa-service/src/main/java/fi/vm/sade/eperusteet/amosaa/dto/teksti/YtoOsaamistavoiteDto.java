package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YtoOsaamistavoiteDto {
    private Long id;
    private LokalisoituTekstiDto selite;
}

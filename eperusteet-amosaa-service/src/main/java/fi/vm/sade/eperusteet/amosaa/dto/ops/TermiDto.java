package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermiDto {
    private Long id;
    private String avain;
    private Boolean alaviite;
    private LokalisoituTekstiDto termi;
    private LokalisoituTekstiDto selitys;
}

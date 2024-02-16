package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmmattitaitovaatimusDto {
    private Long id;
    private LokalisoituTekstiDto selite;
    private String ammattitaitovaatimusKoodi;
    private Integer jarjestys;
}

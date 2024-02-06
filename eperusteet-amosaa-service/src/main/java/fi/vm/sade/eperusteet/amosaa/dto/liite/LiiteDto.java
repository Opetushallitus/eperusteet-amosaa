package fi.vm.sade.eperusteet.amosaa.dto.liite;

import java.util.Date;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiiteDto {
    private UUID id;
    private String tyyppi;
    private String nimi;
    private Date luotu;
}

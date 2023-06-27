package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SisaltoViiteTutkinnonosaKevytDto {
    private Long id;
    private TekstiKappaleDto tekstiKappale;
    private TutkinnonOsaKevytDto tosa;
    private Long linkattuPeruste;
}

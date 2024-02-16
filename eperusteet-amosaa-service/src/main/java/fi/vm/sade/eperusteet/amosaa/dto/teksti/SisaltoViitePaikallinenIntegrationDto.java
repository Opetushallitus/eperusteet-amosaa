package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoViitePaikallinenIntegrationDto {
    private Long id;
    private TekstiKappaleNimiDto tekstiKappale;
    private TutkinnonOsaIntegrationDto tosa;
    private Reference owner;
}

package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleNimiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TutkinnonOsaSijaintiDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoViiteSijaintiDto {
    private Long id;
    private TekstiKappaleNimiDto tekstiKappale;
    private OpetussuunnitelmaBaseDto owner;
    private TutkinnonOsaSijaintiDto tosa;
}


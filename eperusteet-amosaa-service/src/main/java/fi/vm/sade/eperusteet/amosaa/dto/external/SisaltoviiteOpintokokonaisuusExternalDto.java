package fi.vm.sade.eperusteet.amosaa.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoviiteOpintokokonaisuusExternalDto {
    private Long id;
    private Long opetussuunnitelmaId;
}

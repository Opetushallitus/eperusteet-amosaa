package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

import java.util.Set;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KoulutustoimijaDto extends KoulutustoimijaBaseDto {
    LokalisoituTekstiDto kuvaus;
    private Set<Reference> ystavat;
    private boolean salliystavat;
}

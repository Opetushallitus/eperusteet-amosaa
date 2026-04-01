package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKevytDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Sisältöviite, johon on täytetty linkittävän opetussuunnitelman tunniste ja nimi.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SisaltoViiteLinkittavaDto extends SisaltoViiteDto {
    private OpetussuunnitelmaKevytDto opetussuunnitelma;
}

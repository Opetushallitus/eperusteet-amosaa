package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Date;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PoistettuDto extends RevisionDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private Reference koulutustoimija;
    private Reference opetussuunnitelma;
    private Long poistettu;
    private SisaltoTyyppi tyyppi;
    private Date pvm;
    private String muokkaajaOid;
}

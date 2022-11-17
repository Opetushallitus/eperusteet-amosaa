package fi.vm.sade.eperusteet.amosaa.dto.tilastot;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToimijanTilastoDto {
    private KoulutustoimijaBaseDto koulutustoimija;
    private Long julkaistu;
    private Long valmis;
    private Long luonnos;
}

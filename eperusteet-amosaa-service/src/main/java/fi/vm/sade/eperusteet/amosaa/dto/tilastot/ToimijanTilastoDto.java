package fi.vm.sade.eperusteet.amosaa.dto.tilastot;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToimijanTilastoDto {
    private KoulutustoimijaBaseDto koulutustoimija;
    private Long julkaistu;
    private Long valmis;
    private Long luonnos;
}

package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import lombok.Data;

@Data
public class RevisionKayttajaDto extends RevisionDto {
    private KayttajanTietoDto kayttajanTieto;
}

package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TyoryhmaOikeusDto {
    Long id;
    String henkiloOid;
    HenkiloOikeus oikeus;
}

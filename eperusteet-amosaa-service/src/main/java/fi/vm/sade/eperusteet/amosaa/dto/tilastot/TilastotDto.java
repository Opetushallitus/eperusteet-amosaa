package fi.vm.sade.eperusteet.amosaa.dto.tilastot;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TilastotDto {
    private Long koulutuksenjarjestajia;
    private Long opetussuunnitelmia;
    private Long kayttajia;
}

package fi.vm.sade.eperusteet.amosaa.dto.tilastot;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToimijaTilastotDto {
    List<ToimijanTilastoDto> toimijat = new ArrayList<>();
}

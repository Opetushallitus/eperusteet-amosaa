package fi.vm.sade.eperusteet.amosaa.dto.tilastot;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ToimijaTilastotDto {
    List<ToimijanTilastoDto> toimijat = new ArrayList<>();
}

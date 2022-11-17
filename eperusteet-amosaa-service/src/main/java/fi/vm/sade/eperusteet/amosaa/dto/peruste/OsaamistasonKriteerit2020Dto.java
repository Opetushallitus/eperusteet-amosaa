package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsaamistasonKriteerit2020Dto {
    private OsaamistasoDto osaamistaso;
    private List<LokalisoituTekstiDto> kriteerit = new ArrayList<>();
}

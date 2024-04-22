package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArviointiasteikkoDto {
    private Long id;
    private List<OsaamistasoDto> osaamistasot;
}

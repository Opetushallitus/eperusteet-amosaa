package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("osaamistavoite")
public class OsaamisenTavoiteDto {
    private Long id;
    private LokalisoituTekstiDto kohde;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto selite;
    private List<LokalisoituTekstiDto> tavoitteet;
}

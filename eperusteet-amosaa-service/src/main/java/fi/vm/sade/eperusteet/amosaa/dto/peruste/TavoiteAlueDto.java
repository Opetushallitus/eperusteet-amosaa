package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TavoiteAlueDto {

    private Long id;
    private TavoiteAlueTyyppi tavoiteAlueTyyppi;
    private KoodiDto otsikko;
    private List<KoodiDto> tavoitteet = new ArrayList<>();
    private List<LokalisoituTekstiDto> keskeisetSisaltoalueet = new ArrayList<>();
}

package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class KayttooikeusKyselyDto {

    private Map<String, List<String>> kayttooikeudet;
    private List<String> organisaatioOids;

}

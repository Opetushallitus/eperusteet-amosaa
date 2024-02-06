package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedPerusteBaseDto {
    private Long id;
    private String diaarinumero;
    private Long perusteId;
    private LokalisoituTekstiDto nimi;
    private KoulutusTyyppi koulutustyyppi;

    @JsonProperty("koulutukset")
    private Set<KoulutusDto> koulutuskoodit;

}

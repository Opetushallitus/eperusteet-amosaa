package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaamistasonKriteeriDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArvioinninKohdeExportDto {
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto selite;
    private ArviointiasteikkoDto arviointiasteikko;
    private Set<OsaamistasonKriteeriDto> osaamistasonKriteerit;

    @JsonProperty("_arviointiasteikko")
    private Reference arviointiasteikkoRef;
}

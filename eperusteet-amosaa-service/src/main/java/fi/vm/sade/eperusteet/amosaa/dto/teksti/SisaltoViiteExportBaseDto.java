package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteKevytDto;
import lombok.*;

/**
 * Käytetään kaikkien sisältösolmujen pohjana nimen ja tyyppitiedot viemiseen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SisaltoViiteExportBaseDto {
    private Long id;
    private SisaltoTyyppi tyyppi;
    private CachedPerusteKevytDto peruste;

    private Long linkattuOps;
    private Long linkattuSisaltoViiteId;
    private Long linkattuPerusteId;
    @JsonIgnore
    private SisaltoTyyppi linkattuTyyppi;

    public SisaltoTyyppi getTyyppi() {
        if (linkattuTyyppi != null) {
            return linkattuTyyppi;
        }

        return tyyppi;
    }
}

package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.AmmattitaitovaatimusKohdealueetDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.List;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsaamistavoiteLaajaDto extends OsaamistavoiteDto {
    private LokalisoituTekstiDto tavoitteet;
    private LokalisoituTekstiDto tunnustaminen;
    private ArviointiDto arviointi;
    private List<AmmattitaitovaatimusKohdealueetDto> ammattitaitovaatimuksetLista;
    private String koodiUri;
    private String koodiArvo;
    private Reference esitieto;
    private KoodiDto koodi;
}

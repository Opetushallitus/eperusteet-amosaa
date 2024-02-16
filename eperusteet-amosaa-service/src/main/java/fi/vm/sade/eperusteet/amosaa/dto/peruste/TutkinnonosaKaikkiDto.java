package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.AmmattitaitovaatimusKohdealueetDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

import java.util.List;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TutkinnonosaKaikkiDto extends PerusteenOsaDto {
    private final String osanTyyppi = "tutkinnonosa";
    private LokalisoituTekstiDto tavoitteet;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ArviointiDto arviointi;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AmmattitaitovaatimusKohdealueetDto> ammattitaitovaatimuksetLista;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LokalisoituTekstiDto ammattitaitovaatimukset;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LokalisoituTekstiDto ammattitaidonOsoittamistavat;
    private LokalisoituTekstiDto kuvaus;
    private Long opintoluokitus;
    private String koodiUri;
    private String koodiArvo;
    private List<OsaAlueKaikkiDto> osaAlueet;
    private TutkinnonOsaTyyppi tyyppi;
    private ValmaTelmaSisaltoDto valmaTelmaSisalto;
}

package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OmaTutkinnonosaBaseDto {
    private Long id;
    private String koodi;
    private BigDecimal laajuus;
    private Long geneerinenarviointi;
    private PaikallisetAmmattitaitovaatimukset2019Dto ammattitaitovaatimukset;
    private LokalisoituTekstiDto ammattitaidonOsoittamistavat;

    @Deprecated
    private List<AmmattitaitovaatimuksenKohdealueDto> ammattitaitovaatimuksetLista;

    @Deprecated
    private LokalisoituTekstiDto tavoitteet;
}

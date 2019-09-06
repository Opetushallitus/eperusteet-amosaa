package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KayttoikeusKayttajaDto {

    private String oid;
    private String etunimet;
    private String kutsumanimi;
    private String sukunimi;
    private String asiointikieli;
    private String sahkoposti;
    
}

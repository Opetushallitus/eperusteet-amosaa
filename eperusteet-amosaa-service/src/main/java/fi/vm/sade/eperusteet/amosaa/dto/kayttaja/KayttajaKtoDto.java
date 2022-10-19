package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KayttajaKtoDto extends KayttajaDto {
    private String organisaatioOid;

    public KayttajaKtoDto withOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
        return this;
    }
}

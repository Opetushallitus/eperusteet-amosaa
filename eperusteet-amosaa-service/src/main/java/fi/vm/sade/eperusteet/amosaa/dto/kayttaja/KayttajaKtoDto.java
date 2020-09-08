package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import lombok.Data;

@Data
public class KayttajaKtoDto extends KayttajaDto {
    private String organisaatioOid;

    public KayttajaKtoDto withOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
        return this;
    }
}

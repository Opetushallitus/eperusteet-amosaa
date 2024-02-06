package fi.vm.sade.eperusteet.amosaa.dto.kayttaja;

import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KayttajaDto extends KayttajaBaseDto {
    private String etunimet;
    private String kutsumanimi;
    private String sukunimi;
    private Date tiedotekuittaus;
    private Set<Reference> suosikit;

}

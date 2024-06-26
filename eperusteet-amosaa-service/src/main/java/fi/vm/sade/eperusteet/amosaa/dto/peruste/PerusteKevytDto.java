package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerusteKevytDto implements Serializable {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private String diaarinumero;
    private Date voimassaoloAlkaa;
    private KoulutusTyyppi koulutustyyppi;
}

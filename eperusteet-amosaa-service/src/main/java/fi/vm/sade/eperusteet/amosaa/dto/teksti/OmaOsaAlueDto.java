package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OmaOsaAlueDto {
    private Long id;
    private OmaOsaAlueTyyppi tyyppi;
    private String perusteenOsaAlueKoodi;
    private Long perusteenOsaAlueId;
    private LokalisoituTekstiDto tavatjaymparisto;
    private LokalisoituTekstiDto arvioinnista;
    private boolean piilotettu;
    private List<VapaaTekstiDto> vapaat = new ArrayList<>();
    private List<OsaAlueenOsaamistavoitteetDto> ammattitaitovaatimukset = new ArrayList<>();
}

package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaKaikkiDto extends OpetussuunnitelmaDto {
    SisaltoViiteExportDto sisalto;
    List<SuorituspolkuRakenneDto> suorituspolut;
    List<SisaltoViiteExportDto> tutkinnonOsat;
    List<SisaltoViiteExportOpintokokonaisuusDto> opintokokonaisuudet;

    public KoulutusTyyppi getKoulutustyyppi() {
        if (super.getKoulutustyyppi() != null) {
            return super.getKoulutustyyppi();
        } else if (getPeruste() != null) {
            return getPeruste().getKoulutustyyppi();
        }

        return null;
    }
}

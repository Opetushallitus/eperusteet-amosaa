package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import java.util.List;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpetussuunnitelmaKaikkiDto extends OpetussuunnitelmaDto {
    SisaltoViiteExportDto sisalto;
    List<SuorituspolkuRakenneDto> suorituspolut;
    List<TutkinnonosaExportDto> tutkinnonOsat;
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

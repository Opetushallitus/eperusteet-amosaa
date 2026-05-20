package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Julkaistun opetussuunnitelman tiedot sisältörakenteen kanssa.")
public class OpetussuunnitelmaKaikkiDto extends OpetussuunnitelmaDto {
    @Schema(description = "Opetussuunnitelman sisältörakenne.")
    SisaltoViiteExportDto sisalto;
    @Schema(description = "Opetussuunnitelman suorituspolut ja niiden rakenne.")
    List<SuorituspolkuRakenneDto> suorituspolut;
    @Schema(description = "Opetussuunnitelman tutkinnon osat.")
    List<SisaltoViiteExportDto> tutkinnonOsat;
    @Schema(description = "Opetussuunnitelman opintokokonaisuudet.")
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

package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Käytetään sisällön viemiseen rakenteisena
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SisaltoViiteExportDto extends SisaltoViiteExportBaseDto {
    private boolean naytaPohjanTeksti;
    private boolean naytaPerusteenTeksti;
    private LokalisoituTekstiDto perusteteksti;
    private TekstiKappaleDto pohjanTekstikappale;
    private TutkinnonosaBaseDto tosa;
    private SuorituspolkuExportDto suorituspolku;
    private TekstiKappaleJulkinenDto tekstiKappale;
    private List<SisaltoViiteExportDto> lapset;
    private OpintokokonaisuusDto opintokokonaisuus;
    private KoulutuksenOsaDto koulutuksenosa;
    private TuvaLaajaAlainenOsaaminenDto tuvaLaajaAlainenOsaaminen;

    public LokalisoituTekstiDto getNimi() {
        if (koulutuksenosa != null) {
            return koulutuksenosa.getNimi();
        }

        if (tuvaLaajaAlainenOsaaminen != null) {
            return tuvaLaajaAlainenOsaaminen.getNimi();
        }

        if (tekstiKappale != null) {
            return tekstiKappale.getNimi();
        }

        return null;
    }
}

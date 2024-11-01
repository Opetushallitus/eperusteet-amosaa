package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SisaltoViiteKevytDto {
    private Long id;
    @JsonProperty("_tekstiKappale")
    private Reference tekstiKappaleRef;
    private TekstiKappaleKevytDto tekstiKappale;
    private TutkinnonOsaKevytDto tosa;
    private SisaltoTyyppi tyyppi;
    private boolean pakollinen;
    private boolean valmis;
    private boolean liikkumaton;
    private Reference vanhempi;
    private CachedPerusteBaseDto peruste;
    private List<Reference> lapset;
    private KoulutuksenOsaKevytDto koulutuksenosa;
    private TuvaLaajaAlainenOsaaminenDto tuvaLaajaAlainenOsaaminen;
    private SisaltoTyyppi linkattuTyyppi;
    private OpintokokonaisuusDto opintokokonaisuus;
    private Long perusteenOsaId;
    private List<OmaOsaAlueKevytDto> osaAlueet = new ArrayList<>();
    private boolean piilotettu;

    public LokalisoituTekstiDto getNimi() {
        if (SisaltoTyyppi.OSAAMISMERKKI.equals(tyyppi)) {
            return LokalisoituTekstiDto.of("Kansalliset perustaitojen osaamismerkit");
        }

        if (koulutuksenosa != null) {
            return koulutuksenosa.getNimi();
        }

        if (tuvaLaajaAlainenOsaaminen != null) {
            return tuvaLaajaAlainenOsaaminen.getNimi();
        }

        if (opintokokonaisuus != null && opintokokonaisuus.getKooditettuNimi() != null) {
            return opintokokonaisuus.getKooditettuNimi();
        }

        if (tekstiKappale != null) {
            return tekstiKappale.getNimi();
        }

        return null;
    }

    @JsonIgnore
    public Integer getNavikaatioJarjestys() {
        switch (tyyppi) {
            case SUORITUSPOLUT:
                return 1;
            case TUTKINNONOSAT:
                return 2;
            default:
                return 3;
        }
    }
}

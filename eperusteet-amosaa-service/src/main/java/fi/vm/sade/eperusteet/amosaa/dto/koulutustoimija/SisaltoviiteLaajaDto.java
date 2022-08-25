package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KotoKielitaitotasoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KotoLaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KotoOpintoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KoulutuksenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TutkinnonosaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TuvaLaajaAlainenOsaaminenDto;
import lombok.Data;

@Data
public class SisaltoviiteLaajaDto {
    private Long id;
    private TekstiKappaleDto tekstiKappale;
    private SisaltoTyyppi tyyppi;
    @JsonIgnore
    private OpetussuunnitelmaDto owner;

    private CachedPerusteBaseDto peruste;
    private TutkinnonosaDto tosa;
    private SuorituspolkuDto suorituspolku;
    private OpintokokonaisuusDto opintokokonaisuus;
    private KoulutuksenOsaDto koulutuksenosa;
    private TuvaLaajaAlainenOsaaminenDto tuvaLaajaAlainenOsaaminen;
    private KotoKielitaitotasoDto kotoKielitaitotaso;
    private KotoOpintoDto kotoOpinto;
    private KotoLaajaAlainenOsaaminenDto kotoLaajaAlainenOsaaminen;
    private Long perusteenOsaId;

    public OpetussuunnitelmaDto getOpetussuunnitelma() {
        return owner;
    }
}

package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaDto;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Käytetään sisällön viemiseen rakenteisena
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SisaltoViiteExportDto extends SisaltoViiteExportBaseDto {
    private boolean naytaPohjanTeksti;
    private boolean naytaPerusteenTeksti;
    private LokalisoituTekstiDto perusteteksti;
    private TekstiKappaleDto pohjanTekstikappale;
    private TutkinnonosaExportDto tosa;
    private SuorituspolkuExportDto suorituspolku;
    private TekstiKappaleJulkinenDto tekstiKappale;
    private List<SisaltoViiteExportDto> lapset;
    private OpintokokonaisuusDto opintokokonaisuus;
    private KoulutuksenOsaDto koulutuksenosa;
    private TuvaLaajaAlainenOsaaminenDto tuvaLaajaAlainenOsaaminen;
    private KotoKielitaitotasoExportDto kotoKielitaitotaso;
    private KotoOpintoExportDto kotoOpinto;
    private KotoLaajaAlainenOsaaminenExportDto kotoLaajaAlainenOsaaminen;
    private Long perusteenOsaId;
    private List<OmaOsaAlueExportDto> osaAlueet = new ArrayList<>();

    public LokalisoituTekstiDto getNimi() {
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

    public void setPerusteSisalto(PerusteenOsaDto perusteenOsaDto) {
        if (kotoKielitaitotaso != null && perusteenOsaDto instanceof fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoKielitaitotasoDto) {
            kotoKielitaitotaso.setPerusteenOsa((fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoKielitaitotasoDto) perusteenOsaDto);
        }
        if (kotoOpinto != null && perusteenOsaDto instanceof fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoOpintoDto) {
            kotoOpinto.setPerusteenOsa((fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoOpintoDto) perusteenOsaDto);
        }
        if (kotoLaajaAlainenOsaaminen != null && perusteenOsaDto instanceof fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlainenOsaaminenDto) {
            kotoLaajaAlainenOsaaminen.setPerusteenOsa((fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlainenOsaaminenDto) perusteenOsaDto);
        }

        if (perusteenOsaDto instanceof fi.vm.sade.eperusteet.amosaa.dto.peruste.TekstiKappaleDto) {
            perusteteksti = ((fi.vm.sade.eperusteet.amosaa.dto.peruste.TekstiKappaleDto) perusteenOsaDto).getTeksti();
        }
    }

    public List<OmaOsaAlueExportDto> getOsaAlueet() {
        if (CollectionUtils.isNotEmpty(this.osaAlueet)) {
            return this.osaAlueet.stream().filter(osaAlue -> !osaAlue.isPiilotettu()).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}

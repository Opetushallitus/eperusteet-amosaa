/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.amosaa.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;

import java.util.List;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.CachedPerusteBaseDto;
import lombok.*;

/**
 * @author nkala
 */
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

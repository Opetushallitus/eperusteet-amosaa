package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlue;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Osaamistavoite2020Dto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;

public class OpetussuunnitelmaSisaltoCreateUtil {

    public static SisaltoViite perusteenTutkinnonosaToSisaltoviite(SisaltoViite parent, TutkinnonosaKaikkiDto tutkinnonosaKaikkiDto) {
        SisaltoViite uusi = SisaltoViite.createTutkinnonOsa(parent);
        uusi.setPakollinen(false);
        uusi.getTekstiKappale().setNimi(LokalisoituTeksti.of(tutkinnonosaKaikkiDto.getNimi()));
        Tutkinnonosa uusiTosa = uusi.getTosa();
        uusiTosa.setTyyppi(TutkinnonosaTyyppi.PERUSTEESTA);
        uusiTosa.setPerusteentutkinnonosa(tutkinnonosaKaikkiDto.getId());
        uusiTosa.setKoodi(tutkinnonosaKaikkiDto.getKoodiUri());

        for (OsaAlueKaikkiDto perusteenOsaAlue : tutkinnonosaKaikkiDto.getOsaAlueet()) {
            addPerusteenOsaAlueToSisaltoViite(uusi, perusteenOsaAlue);
        }

        return uusi;
    }

    public static void addPerusteenOsaAlueToSisaltoViite(SisaltoViite sisaltoviite, OsaAlueKaikkiDto perusteenOsaAlue) {
        Osaamistavoite2020Dto pakolliset = perusteenOsaAlue.getPakollisetOsaamistavoitteet();
        Osaamistavoite2020Dto valinnaiset = perusteenOsaAlue.getValinnaisetOsaamistavoitteet();

        if (pakolliset != null) {
            OmaOsaAlue oa = new OmaOsaAlue();
            oa.setNimi(LokalisoituTeksti.of(perusteenOsaAlue.getNimi()));
            oa.setTyyppi(OmaOsaAlueTyyppi.PAKOLLINEN);
            oa.setPiilotettu(false);
            oa.setPerusteenOsaAlueKoodi(perusteenOsaAlue.getKoodiUri());
            oa.setPerusteenOsaAlueId(perusteenOsaAlue.getId());
            sisaltoviite.getOsaAlueet().add(oa);
        }

        if (valinnaiset != null) {
            OmaOsaAlue oa = new OmaOsaAlue();
            oa.setNimi(LokalisoituTeksti.of(perusteenOsaAlue.getNimi()));
            oa.setTyyppi(OmaOsaAlueTyyppi.VALINNAINEN);
            oa.setPiilotettu(false);
            oa.setPerusteenOsaAlueKoodi(perusteenOsaAlue.getKoodiUri());
            oa.setPerusteenOsaAlueId(perusteenOsaAlue.getId());
            sisaltoviite.getOsaAlueet().add(oa);
        }
    }
}

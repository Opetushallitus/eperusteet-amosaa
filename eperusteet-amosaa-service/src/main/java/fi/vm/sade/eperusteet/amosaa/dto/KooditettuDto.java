package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

import java.util.Date;

public interface KooditettuDto {

    default void setKooditettu(LokalisoituTekstiDto kooditettu) {}

    default void setKooditettu(LokalisoituTekstiDto kooditettu, Date voimassaAlkuPvm, Date voimassaLoppuPvm) {
        setKooditettu(kooditettu);
    }

    default void setKoodistoKoodi(KoodistoKoodiDto koodi) {
        setKooditettu(koodi.getNimi(), koodi.getVoimassaAlkuPvm(), koodi.getVoimassaLoppuPvm());
    }
}

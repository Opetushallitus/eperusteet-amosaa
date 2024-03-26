package fi.vm.sade.eperusteet.amosaa.dto;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;

import java.util.Date;

public interface KooditettuDto {

    void setKooditettu(LokalisoituTekstiDto kooditettu, Date voimassaAlkuPvm, Date voimassaLoppuPvm);
}

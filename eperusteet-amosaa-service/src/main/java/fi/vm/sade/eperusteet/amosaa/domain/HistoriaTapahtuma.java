package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import java.util.Date;

public interface HistoriaTapahtuma {

    Date getLuotu();

    Date getMuokattu();

    String getLuoja();

    String getMuokkaaja();

    Long getId();

    LokalisoituTeksti getNimi();

    NavigationType getNavigationType();
}

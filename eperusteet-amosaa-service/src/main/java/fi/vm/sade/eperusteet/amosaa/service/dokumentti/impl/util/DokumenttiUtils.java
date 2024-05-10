package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.LaajuusYksikko;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Date;

public class DokumenttiUtils {
    public static final int MAX_TIME_IN_MINUTES = 60;

    public static boolean isTimePass(Dokumentti dokumentti) {
        return (dokumentti.getTila().equals(DokumenttiTila.LUODAAN) || dokumentti.getTila().equals(DokumenttiTila.JONOSSA)) && isTimePass(dokumentti.getAloitusaika());
    }

    public static boolean isTimePass(DokumenttiDto dokumenttiDto) {
        return isTimePass(dokumenttiDto.getAloitusaika());
    }

    public static boolean isTimePass(Date date) {
        if (date == null) {
            return true;
        }

        Date newDate = DateUtils.addMinutes(date, MAX_TIME_IN_MINUTES);
        return newDate.before(new Date());
    }
}

package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.LaajuusYksikko;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * @author isaul
 */
public class DokumenttiUtils {
    private static final int MAX_TIME_IN_MINUTES = 5;

    public static void addLokalisoituteksti(DokumenttiBase docBase, LokalisoituTekstiDto lTekstiDto, String tagi) {
        if (lTekstiDto != null) {
            addLokalisoituteksti(docBase, docBase.getMapper().map(lTekstiDto, LokalisoituTeksti.class), tagi);
        }
    }

    public static void addLokalisoituteksti(DokumenttiBase docBase, LokalisoituTeksti lTeksti, String tagi) {
        addLokalisoituteksti(docBase, lTeksti, tagi, null);
    }

    public static void addLokalisoituteksti(DokumenttiBase docBase, LokalisoituTeksti lTeksti, String tagi, Element el) {
        if (lTeksti != null && lTeksti.getTeksti() != null && lTeksti.getTeksti().get(docBase.getKieli()) != null) {
            String teksti = lTeksti.getTeksti().get(docBase.getKieli());
            teksti = "<" + tagi + ">" + unescapeHtml5(teksti) + "</" + tagi + ">";

            Document tempDoc = new W3CDom().fromJsoup(Jsoup.parseBodyFragment(teksti));
            Node node = tempDoc.getDocumentElement().getChildNodes().item(1).getFirstChild();

            if (el != null) {
                el.appendChild(docBase.getDocument().importNode(node, true));
            } else {
                docBase.getBodyElement().appendChild(docBase.getDocument().importNode(node, true));
            }
        }
    }

    public static void addTeksti(DokumenttiBase docBase, String teksti, String tagi) {
        addTeksti(docBase, teksti, tagi, docBase.getBodyElement());
    }

    public static void addTeksti(DokumenttiBase docBase, String teksti, String tagi, Element element) {
        if (teksti != null) {

            teksti = unescapeHtml5(teksti);
            teksti = "<" + tagi + ">" + teksti + "</" + tagi + ">";

            Document tempDoc = new W3CDom().fromJsoup(Jsoup.parseBodyFragment(teksti));
            Node node = tempDoc.getDocumentElement().getChildNodes().item(1).getFirstChild();

            element.appendChild(docBase.getDocument().importNode(node, true));
        }
    }

    public static void addHeader(DokumenttiBase docBase, String text) {
        if (text != null) {
            Element header = docBase.getDocument().createElement("h" + docBase.getGenerator().getDepth());
            header.setAttribute("number", docBase.getGenerator().generateNumber());
            header.appendChild(docBase.getDocument().createTextNode(unescapeHtml5(text)));
            docBase.getBodyElement().appendChild(header);
        }
    }

    public static String getTextString(DokumenttiBase docBase, LokalisoituTekstiDto lokalisoituTekstiDto) {
        LokalisoituTeksti lokalisoituTeksti = docBase.getMapper().map(lokalisoituTekstiDto, LokalisoituTeksti.class);
        return getTextString(docBase, lokalisoituTeksti);
    }

    public static String getTextString(DokumenttiBase docBase, LokalisoituTeksti lokalisoituTeksti) {
        if (lokalisoituTeksti == null || lokalisoituTeksti.getTeksti() == null
                || lokalisoituTeksti.getTeksti().get(docBase.getKieli()) == null) {
            return "";
        } else {
            return unescapeHtml5(lokalisoituTeksti.getTeksti().get(docBase.getKieli()));
        }
    }

    public static String unescapeHtml5(String string) {
        return StringEscapeUtils.unescapeHtml4(Jsoup.clean(stripNonValidXMLCharacters(string), ValidHtml.WhitelistType.NORMAL.getWhitelist()));
    }

    public static String stripNonValidXMLCharacters(String in) {
        StringBuilder out = new StringBuilder();
        char current;

        if (in == null || ("".equals(in))) return "";
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if (current == 0x9
                    || current == 0xA
                    || current == 0xD
                    || current >= 0x20 && current <= 0xD7FF
                    || current >= 0xE000 && current <= 0xFFFD) {
                out.append(current);
            }
        }

        return out.toString();
    }

    public static boolean isTimePass(Dokumentti dokumentti) {
        return isTimePass(dokumentti.getAloitusaika());
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

    public static String selectLaajuusYksikkoMessage(LaajuusYksikko laajuusYksikko) {
        switch (laajuusYksikko) {
            case OPINTOVIIKKO:
                return "docgen.laajuus.ov";
            case OPINTOPISTE:
                return "docgen.laajuus.op";
            case OSAAMISPISTE:
                return "docgen.laajuus.osp";
            case KURSSI:
                return "docgen.laajuus.kurssi";
            case TUNTI:
                return "docgen.laajuus.tunti";
            case VIIKKO:
                return "docgen.laajuus.viikkoa";
            case VUOSIVIIKKOTUNTI:
                return "docgen.laajuus.vuosiviikkotunti";
            case VUOSI:
                return "docgen.laajuus.vuosi";
            default:
                return "docgen.laajuus.op"; // palautetaan 'op', joka oli default ennen laajuusyksikön tuloa
        }
    }
}

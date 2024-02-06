package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class DokumenttiTaulukko {
    public static final String TABLE_HEADER_BGCOLOR = "#d4e3f4";

    private String otsikko;
    private ArrayList<String> otsikkoSarakkeet = new ArrayList<>();
    private ArrayList<DokumenttiRivi> rivit = new ArrayList<>();

    public void addOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public void addOtsikkoSarake(String sarake) {
        otsikkoSarakkeet.add(sarake);
    }

    public void addRivi(DokumenttiRivi rivi) {
        rivit.add(rivi);
    }

    public void addToDokumentti(DokumenttiBase docBase) {
        if (rivit.size() > 0) {
            Document tempDoc = new W3CDom().fromJsoup(Jsoup.parseBodyFragment(this.toString()));
            Node node = tempDoc.getDocumentElement().getChildNodes().item(1).getFirstChild();
            docBase.getBodyElement().appendChild(docBase.getDocument().importNode(node, true));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<div>");

        // Tyhjää taulukkoa on turha antaa
        if (otsikko != null) {
            builder.append("<strong>");
            builder.append(otsikko);
            builder.append("</strong>");
        }

        builder.append("<table border=\"1\">");

        // Otsikko rivi
        if (otsikkoSarakkeet.size() > 0) {
            builder.append("<tr bgcolor=\"" + TABLE_HEADER_BGCOLOR + " \">");
            otsikkoSarakkeet.forEach(sarake -> {
                builder.append("<th>");
                builder.append(sarake);
                builder.append("</th>");
            });
            builder.append("</tr>");
        }

        rivit.forEach(rivi -> {
            builder.append("<tr>");
            builder.append(rivi.toString());
            builder.append("</tr>");
        });

        builder.append("</table>");

        builder.append("</div>");
        return builder.toString();
    }


    public static void addRow(DokumenttiBase docBase, Element taulukko, String teksti) {
        addRow(docBase, taulukko, teksti, false);
    }

    public static void addRow(DokumenttiBase docBase, Element taulukko, String teksti, boolean header) {
        Element tr = docBase.getDocument().createElement("tr");
        taulukko.appendChild(tr);
        if (header) {
            tr.setAttribute("bgcolor", TABLE_HEADER_BGCOLOR);
        }

        if (header) {
            Element th = docBase.getDocument().createElement("th");
            th.appendChild(newBoldElement(docBase.getDocument(), teksti));
            tr.appendChild(th);
        } else {
            Element td = docBase.getDocument().createElement("td");
            td.appendChild(newBoldElement(docBase.getDocument(), teksti));
            tr.appendChild(td);
        }
    }

    public static Element newBoldElement(Document doc, String teksti) {
        Element strong = doc.createElement("strong");
        strong.appendChild(doc.createTextNode(teksti));
        return strong;
    }
}

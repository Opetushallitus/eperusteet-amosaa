package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.custom;

public final class RepositoryUtil {

    private RepositoryUtil() {
        //apuluokka
    }

    public static final char ESCAPE_CHAR = '\\';

    public static String kuten(String teksti) {
        StringBuilder b = new StringBuilder("%");
        b.append(teksti.toLowerCase().replace("" + ESCAPE_CHAR, "" + ESCAPE_CHAR + ESCAPE_CHAR).replace("_", ESCAPE_CHAR
                + "_").replace("%", ESCAPE_CHAR + "%"));
        b.append("%");
        return b.toString();
    }

}

package fi.vm.sade.eperusteet.amosaa.service.security;

public enum Permission {

    ESITYS("esitys"), // LUKU oikeus esikatselussa ja julkaistussa ilman kirjautumista
    LUKU("luku"),
    MUOKKAUS("muokkaus"),
    KOMMENTOINTI("kommentointi"),
    LUONTI("luonti"),
    POISTO("poisto"),
    TILANVAIHTO("tilanvaihto"),
    HALLINTA("hallinta");

    private final String permission;

    private Permission(String permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return permission;
    }

    public boolean isOneOf(Permission... tyypit) {
        for (Permission toinen : tyypit) {
            if (toinen.toString().equals(this.permission)) {
                return true;
            }
        }
        return false;
    }
}

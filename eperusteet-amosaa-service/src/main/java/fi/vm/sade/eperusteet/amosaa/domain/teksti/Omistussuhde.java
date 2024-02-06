package fi.vm.sade.eperusteet.amosaa.domain.teksti;

public enum Omistussuhde {
    OMA("oma"),
    LAINATTU("lainattu");

    private String omistussuhde;

    private Omistussuhde(String omistussuhde) {
        this.omistussuhde = omistussuhde;
    }

    @Override
    public String toString() {
        return omistussuhde;
    }
}

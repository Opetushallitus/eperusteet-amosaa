package fi.vm.sade.eperusteet.amosaa.service.security;

public enum TargetType {
    OPH("oph"),
    KOULUTUSTOIMIJA("koulutustoimija"),
    TARKASTELU("tarkastelu"),
    OPETUSSUUNNITELMA("opetussuunnitelma");

    private final String target;

    private TargetType(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return target;
    }
}

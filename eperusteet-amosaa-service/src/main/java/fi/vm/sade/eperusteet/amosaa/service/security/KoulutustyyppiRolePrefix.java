package fi.vm.sade.eperusteet.amosaa.service.security;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;

public enum KoulutustyyppiRolePrefix {
    KOULUTUSTYYPPI_1_AMOSAA(KoulutusTyyppi.PERUSTUTKINTO, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA),
    KOULUTUSTYYPPI_11_AMOSAA(KoulutusTyyppi.AMMATTITUTKINTO, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA),
    KOULUTUSTYYPPI_12_AMOSAA(KoulutusTyyppi.ERIKOISAMMATTITUTKINTO, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA),
    KOULUTUSTYYPPI_5_AMOSAA(KoulutusTyyppi.TELMA, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA),
    KOULUTUSTYYPPI_18_AMOSAA(KoulutusTyyppi.VALMA, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA),
    KOULUTUSTYYPPI_10_VST(KoulutusTyyppi.VAPAASIVISTYSTYO, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_VST),
    KOULUTUSTYYPPI_30_KOTO(KoulutusTyyppi.MAAHANMUUTTAJIENKOTOUTUMISKOULUTUS, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_KOTO),
    KOULUTUSTYYPPI_35_VST(KoulutusTyyppi.VAPAASIVISTYSTYOLUKUTAITO, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_VST),
    KOULUTUSTYYPPI_40_TUVA(KoulutusTyyppi.TUTKINTOONVALMENTAVA, PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_TUVA);

    private KoulutusTyyppi koulutusTyyppi;
    private PermissionEvaluator.RolePrefix rolePrefix;

    KoulutustyyppiRolePrefix(KoulutusTyyppi koulutusTyyppi, PermissionEvaluator.RolePrefix rolePrefix) {
        this.koulutusTyyppi = koulutusTyyppi;
        this.rolePrefix = rolePrefix;
    }

    public static PermissionEvaluator.RolePrefix of(KoulutusTyyppi koulutusTyyppi) {
        for(KoulutustyyppiRolePrefix ktRolePrefix : values()) {
            if (ktRolePrefix.koulutusTyyppi.equals(koulutusTyyppi)) {
                return ktRolePrefix.rolePrefix;
            }
        }

        return PermissionEvaluator.RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA;
    }
}

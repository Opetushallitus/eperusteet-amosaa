package fi.vm.sade.eperusteet.amosaa.service.security;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.Organization;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePrefix;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Profile("!developmentPermissionOverride")
@Service
public class PermissionManagerImpl extends AbstractPermissionManager {

    private static final String MSG_OPS_EI_OLEMASSA = "Pyydettyä opetussuunnitelmaa ei ole olemassa";

    public boolean hasPermission(Authentication authentication, Serializable targetObject, TargetType target,
                                 Permission perm) {

        Long targetId = null;
        Long originalKtId = null;
        if (targetObject instanceof Long) {
            targetId = (Long) targetObject;
        }
        if (targetObject instanceof List) {
            Object[] arr = ((List) targetObject).toArray();
            originalKtId = (Long) arr[0];
            targetId = (Long) arr[1];
        }

        List<RolePrefix> rolePrefixes = Arrays.asList(
                RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA,
                RolePrefix.ROLE_APP_EPERUSTEET_VST,
                RolePrefix.ROLE_APP_EPERUSTEET_TUVA,
                RolePrefix.ROLE_APP_EPERUSTEET_KOTO);
        if (targetId != null && target.equals(TargetType.OPETUSSUUNNITELMA)) {
            Opetussuunnitelma ops = opsRepository.findOne(targetId);
            if (ops == null) {
                return false;
            }
            rolePrefixes = Arrays.asList(KoulutustyyppiRolePrefix.of(ops.getOpsKoulutustyyppi()));
        }

        if (perm == Permission.HALLINTA && targetId == null && target == TargetType.TARKASTELU &&
                hasRole(authentication, rolePrefixes, RolePermission.CRUD, Organization.OPH)) {
            return true;
        }

        // OPH admin oikeuksilla lukuoikeudet
        if ((perm == Permission.LUKU || perm == Permission.ESITYS) && rolePrefixes.stream().anyMatch(rolePrefix -> SecurityUtil.isUserOphAdmin(rolePrefix))) {
            return true;
        }

        // Käyttäjän luonti mahdollista vain jos käyttäjällä on AMOSAA/VST/TUVA/KOTO-rooli
        if (perm == Permission.LUKU && targetId == null && target == TargetType.KOULUTUSTOIMIJA &&
                hasAppRole(authentication, rolePrefixes)) {
            return true;
        }

        if (perm == Permission.HALLINTA
                && target == TargetType.OPH
                && hasRole(authentication,
                rolePrefixes,
                RolePermission.ADMIN,
                Organization.OPH)) {
            return true;
        }

        // Salli esikatselussa olevien opsien tarkistelu
        Pair<Tyyppi, Tila> tyyppiJaTila =
                targetId != null ? opsRepository.findTyyppiAndTila((long) targetId) : null;

        if (perm == Permission.ESITYS && tyyppiJaTila != null) {
            if (tyyppiJaTila.getSecond() == Tila.JULKAISTU || julkaisuRepository.existsByOpetussuunnitelmaId(targetId)) {
                return true;
            } else if (opsRepository.isEsikatseltavissa((long) targetId)) {
                return true;
            }
        }

        Set<RolePermission> permissions;
        switch (perm) {
            case ESITYS:
            case LUKU:
            case KOMMENTOINTI:
                permissions = EnumSet.allOf(RolePermission.class);
                break;
            case MUOKKAUS:
                permissions = EnumSet.of(RolePermission.CRUD, RolePermission.READ_UPDATE, RolePermission.ADMIN);
                break;
            case TILANVAIHTO:
            case LUONTI:
            case POISTO:
                permissions = EnumSet.of(RolePermission.CRUD, RolePermission.ADMIN);
                break;
            case HALLINTA:
                permissions = EnumSet.of(RolePermission.ADMIN);
                break;
            default:
                permissions = EnumSet.noneOf(RolePermission.class);
                break;
        }

        String organisaatioOid;
        Opetussuunnitelma ops = null;
        Koulutustoimija koulutustoimija = null;

        switch (target) {
            case OPETUSSUUNNITELMA:
                if (targetId == null) {
                    return false;
                }

                ops = opsRepository.findOne(targetId);
                if (ops == null) {
                    return false;
                }
                koulutustoimija = ops.getKoulutustoimija();
                organisaatioOid = koulutustoimija.getOrganisaatio();
                break;
            case KOULUTUSTOIMIJA:
                if (targetId == null) {
                    return false;
                }

                // Optiomoi
                koulutustoimija = koulutustoimijaRepository.findOne(targetId);
                if (koulutustoimija == null) {
                    return false;
                }
                organisaatioOid = koulutustoimija.getOrganisaatio();
                break;
            default:
                organisaatioOid = null;
        }

        Organization organisaatio = organisaatioOid != null ? new Organization(organisaatioOid) : Organization.ANY;

        // Jos käyttäjällä on jokin rooli organisaatiossa niin on lupa lukea
        if ((perm == Permission.ESITYS || perm == Permission.LUKU || perm == Permission.KOMMENTOINTI)
                && hasAnyRole(authentication, rolePrefixes, permissions, organisaatio)) {
            return true;
        }

        // Jos käyttäjällä on organisaatiossa hallintaoikeus
        if (hasRole(authentication, rolePrefixes, RolePermission.ADMIN, organisaatio)) {
            return true;
        }

        // Koulutustoimijan oikeus
        if (target == TargetType.KOULUTUSTOIMIJA) {
            if (!hasAnyRole(authentication, rolePrefixes, permissions, organisaatio)) {
                if (perm == Permission.LUKU) {
                    final Koulutustoimija kohdeKt = koulutustoimija;
                    return rolePrefixes.stream()
                            .anyMatch(rolePrefix ->
                                kayttajanKoulutustoimijat(rolePrefix).stream()
                                        .anyMatch(kayttajanKt -> areFriends(kayttajanKt, kohdeKt)));
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        // Opetussuunnitelmien oikeudet
        else if (target == TargetType.OPETUSSUUNNITELMA) {
            if (hasAnyRole(authentication, rolePrefixes, permissions, organisaatio)) {
                return true;
            }

            KayttajaoikeusTyyppi oikeus = kayttajaoikeusRepository.findKayttajaoikeus(authentication.getName(), targetId);
            if (koulutustoimija == null || ops == null) {
                return false;
            }

            if (oikeus == null || oikeus == KayttajaoikeusTyyppi.ESTETTY) {
                return false;
            }

            if (originalKtId != null && !koulutustoimija.getId().equals(originalKtId)) {
                Koulutustoimija kontekstiKt = koulutustoimijaRepository.findOne(originalKtId);
                if (!areFriends(ops.getKoulutustoimija(), kontekstiKt) || !ops.getTyyppi().isOneOf(OpsTyyppi.OPS, OpsTyyppi.YLEINEN)) {
                    return false;
                }
            }

            if (perm.isOneOf(Permission.LUKU, Permission.KOMMENTOINTI, Permission.ESITYS) && oikeus.isAtLeast(KayttajaoikeusTyyppi.LUKU)) {
                return true;
            }

            if (perm.isOneOf(Permission.MUOKKAUS, Permission.POISTO, Permission.LUONTI) && oikeus.isAtLeast(KayttajaoikeusTyyppi.MUOKKAUS)) {
                return true;
            }

            if (perm.isOneOf(Permission.TILANVAIHTO) && oikeus.isAtLeast(KayttajaoikeusTyyppi.LISAYS)) {
                return true;
            }

            if (perm.isOneOf(Permission.HALLINTA) && oikeus.isAtLeast(KayttajaoikeusTyyppi.HALLINTA)) {
                return true;
            }
        }
        return false;
    }

    private static boolean areFriends(Koulutustoimija a, Koulutustoimija b) {
        return a.isSalliystavat()
                && b.isSalliystavat()
                && a.getYstavat().contains(b)
                && b.getYstavat().contains(a);
    }

    private static boolean hasRole(Authentication authentication, List<RolePrefix> prefixes,
                                   RolePermission permission, Organization org) {
        return hasAnyRole(authentication, prefixes, Collections.singleton(permission), org);
    }

    private static boolean hasAnyRole(Authentication authentication, List<RolePrefix> prefixes,
                                      Set<RolePermission> permission, Organization org) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> permission.stream().anyMatch(p -> prefixes.stream().anyMatch(prefix -> roleEquals(a.getAuthority(), prefix, p, org))));
    }

    private static boolean hasAmosaaRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> {
                    String authority = a.getAuthority();
                    return authority.startsWith(RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA.toString());
                });
    }

    private static boolean hasAppRole(Authentication authentication, List<RolePrefix> rolePrefixes) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> {
                    String authority = a.getAuthority();
                    return rolePrefixes.stream().anyMatch(rolePrefix -> authority.startsWith(rolePrefix.toString()));
                });
    }

    private static boolean roleEquals(String authority, RolePrefix prefix,
                                      RolePermission permission, Organization org) {
        if (Organization.ANY.equals(org)) {
            return authority.equals(prefix.name() + "_" + permission.name());
        } else if (org.getOrganization().isPresent()) {
            return authority.equals(prefix.name() + "_" + permission.name() + "_" + org.getOrganization().get());
        }

        return false;
    }

    private static Set<Permission> fromRolePermission(RolePermission rolePermission) {
        Set<Permission> permissions = new HashSet<>();
        switch (rolePermission) {
            case ADMIN:
                permissions.add(Permission.HALLINTA);
            case CRUD:
                permissions.add(Permission.LUONTI);
                permissions.add(Permission.POISTO);
                permissions.add(Permission.TILANVAIHTO);
            case READ_UPDATE:
                permissions.add(Permission.LUKU);
                permissions.add(Permission.MUOKKAUS);
            case READ:
                permissions.add(Permission.ESITYS);
                permissions.add(Permission.LUKU);
                permissions.add(Permission.KOMMENTOINTI);
                break;
        }
        return permissions;
    }
}

/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.amosaa.service.security;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.Organization;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePrefix;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author mikkom
 */
@Profile("!developmentPermissionOverride")
@Service
public class PermissionManagerImpl extends AbstractPermissionManager {

    private static final String MSG_OPS_EI_OLEMASSA = "Pyydettyä opetussuunnitelmaa ei ole olemassa";

    @Transactional(readOnly = true)
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

        if (perm == Permission.HALLINTA && targetId == null && target == TargetType.TARKASTELU &&
                hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, RolePermission.CRUD, Organization.OPH)) {
            return true;
        }

        // OPH admin oikeuksilla lukuoikeudet
        if ((perm == Permission.LUKU || perm == Permission.ESITYS) && SecurityUtil.isUserOphAdmin()) {
            return true;
        }

        // Käyttäjän luonti mahdollista vain jos käyttäjällä on AMOSAA-rooli
        if (perm == Permission.LUKU && targetId == null && target == TargetType.KOULUTUSTOIMIJA &&
                hasAmosaaRole(authentication)) {
            return true;
        }

        if (perm == Permission.HALLINTA
                && target == TargetType.OPH
                && hasRole(authentication,
                RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA,
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
                && hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, permissions, organisaatio)) {
            return true;
        }

        // Jos käyttäjällä on organisaatiossa hallintaoikeus
        if (hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, RolePermission.ADMIN, organisaatio)) {
            return true;
        }

        // Koulutustoimijan oikeus
        if (target == TargetType.KOULUTUSTOIMIJA) {
            if (!hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, permissions, organisaatio)) {
                if (perm == Permission.LUKU) {
                    final Koulutustoimija kohdeKt = koulutustoimija;
                    return kayttajanKoulutustoimijat().stream().anyMatch(kayttajanKt -> areFriends(kayttajanKt, kohdeKt));
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        // Opetussuunnitelmien oikeudet
        else if (target == TargetType.OPETUSSUUNNITELMA) {
            if (hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, permissions, organisaatio)) {
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

    private static boolean hasRole(Authentication authentication, RolePrefix prefix,
                                   RolePermission permission, Organization org) {
        return hasAnyRole(authentication, prefix, Collections.singleton(permission), org);
    }

    private static boolean hasAnyRole(Authentication authentication, RolePrefix prefix,
                                      Set<RolePermission> permission, Organization org) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> permission.stream().anyMatch(p -> roleEquals(a.getAuthority(), prefix, p, org)));
    }

    private static boolean hasAmosaaRole(Authentication authentication) {
//        RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA
        return authentication.getAuthorities().stream()
                .anyMatch(a -> {
                    String authority = a.getAuthority();
                    return authority.startsWith(RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA.toString());
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

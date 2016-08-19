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
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.Organization;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePrefix;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * @author mikkom
 */
@Service
public class PermissionManager {

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
    }

    private static final String MSG_OPS_EI_OLEMASSA = "Pyydettyä opetussuunnitelmaa ei ole olemassa";

//    @Autowired
//    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Transactional(readOnly = true)
    public boolean hasPermission(Authentication authentication, Serializable targetId, TargetType target,
        Permission perm) {

        if (perm == Permission.HALLINTA && targetId == null && target == TargetType.TARKASTELU &&
                hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, RolePermission.CRUD, Organization.OPH)) {
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
            if (tyyppiJaTila.getSecond() == Tila.JULKAISTU) {
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

                // Optiomoi
                ops = opsRepository.findOne((Long)targetId);
                koulutustoimija = ops.getKoulutustoimija();
                organisaatioOid = koulutustoimija.getOrganisaatio();
                break;
            case KOULUTUSTOIMIJA:
                if (targetId == null) {
                    return false;
                }

                // Optiomoi
                koulutustoimija = koulutustoimijaRepository.findOne((Long)targetId);
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
        if (( perm == Permission.ESITYS || perm == Permission.LUKU || perm == Permission.KOMMENTOINTI)
                && hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, permissions, organisaatio)) {
            return true;
        }

        // Jos käyttäjällä on organisaatiossa hallintaoikeus
        if (hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, RolePermission.ADMIN, organisaatio)) {
            return true;
        }

        if (target == TargetType.KOULUTUSTOIMIJA) {
            return hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, permissions, organisaatio);
        }
        else if (target == TargetType.OPETUSSUUNNITELMA) {
            KayttajaoikeusTyyppi oikeus = kayttajaoikeusRepository.findKayttajaoikeus(authentication.getName(), (Long)targetId);
            return oikeus != null; // FIXME: Tarkista tarkempi oikeus
        }
        return false;
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

    private static boolean roleEquals(String authority, RolePrefix prefix,
        RolePermission permission, Organization org) {
        if (Organization.ANY.equals(org)) {
            return authority.equals(prefix.name() + "_" + permission.name());
        } else if (org.getOrganization().isPresent()) {
            return authority.equals(prefix.name() + "_" + permission.name() + "_" + org.getOrganization().get());
        }

        return false;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Map<RolePermission, Set<Long>> getOrganisaatioOikeudet() {
        return EnumSet.allOf(RolePermission.class).stream()
                .map(r -> new Pair<>(r, SecurityUtil.getOrganizations(Collections.singleton(r)).stream()
                    .map(oid -> koulutustoimijaRepository.findOneByOrganisaatio(oid))
                    .filter(kt -> kt != null)
                    .map(Koulutustoimija::getId)
                    .collect(Collectors.toSet())))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
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

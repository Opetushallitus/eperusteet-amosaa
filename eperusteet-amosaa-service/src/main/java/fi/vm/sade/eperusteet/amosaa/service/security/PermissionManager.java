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

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.Organization;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePrefix;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mikkom
 */
@Service
public class PermissionManager {

    public enum TargetType {
        POHJA("pohja"),
        TARKASTELU("tarkastelu"),
        OPETUSSUUNNITELMA("opetussuunnitelma"),
        KOULUTUSTOIMIJA("koulutustoimija");

        private final String target;

        TargetType(String target) {
            this.target = target;
        }

        @Override
        public String toString() {
            return target;
        }
    }

    public enum Permission {

        LUKU("luku"),
        MUOKKAUS("muokkaus"),
        KOMMENTOINTI("kommentointi"),
        LUONTI("luonti"),
        POISTO("poisto"),
        TILANVAIHTO("tilanvaihto"),
        HALLINTA("hallinta");

        private final String permission;

        Permission(String permission) {
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

    @Transactional(readOnly = true)
    public boolean hasPermission(Authentication authentication, Serializable targetId, TargetType target,
        Permission perm) {

        if (perm == Permission.HALLINTA && targetId == null && target == TargetType.TARKASTELU &&
                hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA, RolePermission.CRUD, Organization.OPH)) {
            return true;
        }

        Set<RolePermission> permissions;
        switch (perm) {
            case LUKU:
            case KOMMENTOINTI:
                permissions = EnumSet.allOf(RolePermission.class);
                break;
            case TILANVAIHTO:
            case LUONTI:
            case POISTO:
                permissions = EnumSet.of(RolePermission.CRUD, RolePermission.ADMIN);
                break;
            case MUOKKAUS:
                permissions = EnumSet.of(RolePermission.CRUD, RolePermission.READ_UPDATE, RolePermission.ADMIN);
                break;
            case HALLINTA:
                permissions = EnumSet.of(RolePermission.ADMIN);
                break;
            default:
                permissions = EnumSet.noneOf(RolePermission.class);
                break;
        }

        switch (target) {
            case POHJA:
            case OPETUSSUUNNITELMA:
                return hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA,
                        permissions, Organization.ANY);
            case KOULUTUSTOIMIJA:
                if (targetId != null) {
                    Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne((Long) targetId);
                    if (koulutustoimija != null) {
                        String orgOid = koulutustoimija.getOrganisaatio();
                        Organization organization = new Organization(orgOid);

                        return hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA,
                                permissions, organization);
                    }
                }
            default:
                return hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_AMOSAA,
                                  permissions, Organization.ANY);
        }
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
        }
        return authority.equals(prefix.name() + "_" + permission.name() + "_" + org.getOrganization().get());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Map<TargetType, Set<Permission>> getOpsPermissions() {
        Map<TargetType, Set<Permission>> permissionMap = new HashMap<>();

        Set<Permission> opsPermissions =
            EnumSet.allOf(RolePermission.class).stream()
                   .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                   .filter(pair -> !pair.getSecond().isEmpty())
                   .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                   .collect(Collectors.toSet());
        permissionMap.put(TargetType.OPETUSSUUNNITELMA, opsPermissions);

        Set<Permission> pohjaPermissions =
            EnumSet.allOf(RolePermission.class).stream()
                   .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                   .filter(pair -> pair.getSecond().contains(SecurityUtil.OPH_OID))
                   .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                   .collect(Collectors.toSet());
        permissionMap.put(TargetType.POHJA, pohjaPermissions);

        return permissionMap;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Set<Permission> getKoulutustoimijaPermissions(Long id) {

        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(id);
        if (koulutustoimija == null) {
            throw new NotExistsException(MSG_OPS_EI_OLEMASSA);
        }

        String organisaatio = koulutustoimija.getOrganisaatio();
        Set<Permission> permissions
            = EnumSet.allOf(RolePermission.class).stream()
            .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
            .filter(pair -> pair.getSecond().contains(organisaatio))
            .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
            .collect(Collectors.toSet());

        return new HashSet<>(permissions);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Map<TargetType, Set<Permission>> getOpsPermissions(Long id) {
        Map<TargetType, Set<Permission>> permissionMap = new HashMap<>();
        return permissionMap;
    }

    private static Set<Permission> fromRolePermission(RolePermission rolePermission) {
        Set<Permission> permissions = new HashSet<>();
        switch (rolePermission) {
            case ADMIN:
                permissions.add(Permission.HALLINTA);
            case CRUD:
                permissions.add(Permission.LUONTI);
                permissions.add(Permission.POISTO);
            case READ_UPDATE:
                permissions.add(Permission.LUKU);
                permissions.add(Permission.MUOKKAUS);
                permissions.add(Permission.TILANVAIHTO);
            case READ:
                permissions.add(Permission.LUKU);
                permissions.add(Permission.KOMMENTOINTI);
                break;
        }
        return permissions;
    }
}

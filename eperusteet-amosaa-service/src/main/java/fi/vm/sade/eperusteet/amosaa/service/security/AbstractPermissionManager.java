package fi.vm.sade.eperusteet.amosaa.service.security;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.Pair;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractPermissionManager implements PermissionManager {

    @Autowired
    protected KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    protected OpetussuunnitelmaRepository opsRepository;

    @Autowired
    protected JulkaisuRepository julkaisuRepository;

    @Autowired
    protected KayttajaoikeusRepository kayttajaoikeusRepository;

    @Transactional(readOnly = true)
    public Map<PermissionEvaluator.RolePermission, Set<Long>> getOrganisaatioOikeudet() {
        return EnumSet.allOf(PermissionEvaluator.RolePermission.class).stream()
                .map(r -> new Pair<>(r, SecurityUtil.getOrganizations(Collections.singleton(r)).stream()
                        .map(oid -> koulutustoimijaRepository.findOneByOrganisaatio(oid))
                        .filter(kt -> kt != null)
                        .map(Koulutustoimija::getId)
                        .collect(Collectors.toSet())))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    @Transactional(readOnly = true)
    public Map<PermissionEvaluator.RolePermission, Set<Koulutustoimija>> getKoulutustoimijaOikeudet() {
        return EnumSet.allOf(PermissionEvaluator.RolePermission.class).stream()
                .map(r -> new Pair<>(r, SecurityUtil.getOrganizations(Collections.singleton(r)).stream()
                        .map(oid -> koulutustoimijaRepository.findOneByOrganisaatio(oid))
                        .filter(kt -> kt != null)
                        .collect(Collectors.toSet())))
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }
}

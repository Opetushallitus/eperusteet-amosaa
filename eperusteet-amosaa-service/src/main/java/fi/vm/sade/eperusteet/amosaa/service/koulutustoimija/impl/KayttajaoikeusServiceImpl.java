package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionManager;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KayttajaoikeusServiceImpl implements KayttajaoikeusService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Lazy
    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private PermissionManager permissionManager;

    @Override
    public List<KayttajaoikeusDto> getKayttooikeudet() {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(kayttajanTietoService.getUserOid());
        List<Kayttajaoikeus> result = kayttajaoikeusRepository.findAllByKayttaja(kayttaja);
        return mapper.mapAsList(result, KayttajaoikeusDto.class);
    }

    @Override
    public ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<Long>>> getOrganisaatiooikeudet(PermissionEvaluator.RolePrefix rolePrefix) {
        return ResponseEntity.ok(permissionManager.getOrganisaatioOikeudet(rolePrefix));
    }

    @Override
    public ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<KoulutustoimijaBaseDto>>> getKoulutustoimijaOikeudet(PermissionEvaluator.RolePrefix rolePrefix) {
        return ResponseEntity.ok(permissionManager.getKoulutustoimijaOikeudet(rolePrefix));
    }
}

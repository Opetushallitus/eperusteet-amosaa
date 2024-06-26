package fi.vm.sade.eperusteet.amosaa.service.external;

import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaKtoDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface KayttajanTietoService {

    // FIXME Tarkasta halutaanko näitä tiukentaa

    @PreAuthorize("isAuthenticated()")
    String getUserOid();

    @PreAuthorize("hasPermission(null, 'koulutustoimija', 'LUKU')")
    KayttajanTietoDto haeKirjautaunutKayttaja();

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto hae(String oid);

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto hae(Long id);

    @PreAuthorize("hasPermission(null, 'koulutustoimija', 'LUKU')")
    Kayttaja getKayttaja();

    @PreAuthorize("isAuthenticated()")
    Future<KayttajanTietoDto> haeAsync(String oid);

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto haeNimi(Long id);

    @PreAuthorize("isAuthenticated()")
    List<KoulutustoimijaBaseDto> koulutustoimijat(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("isAuthenticated()")
    boolean updateKoulutustoimijat(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("isAuthenticated()")
    Set<String> getUserOrganizations(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KayttajaKtoDto> getKaikkiKayttajat(@P("ktId") Long ktId, PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KayttajaKtoDto> getYstavaOrganisaatioKayttajat(@P("ktId") Long ktId, PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    KayttajanTietoDto getKayttaja(@P("ktId") Long ktId, String oid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    KayttajanTietoDto getKayttaja(@P("ktId") Long ktId, Long oid);

    @PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'LUKU')")
    List<KayttajaKtoDto> getKayttajat(@P("ktId") Long ktId, PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    void removeSuosikki(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    void addSuosikki(@P("opsId") Long opsId);

    @PreAuthorize("isAuthenticated()")
    KayttajaDto haeKayttajanTiedot();

    @PreAuthorize("isAuthenticated()")
    KayttajaDto saveKayttaja(String oid);

    @PreAuthorize("isAuthenticated()")
    EtusivuDto haeKayttajanEtusivu(PermissionEvaluator.RolePrefix rolePrefix);

    @PreAuthorize("isAuthenticated()")
    List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid);
}

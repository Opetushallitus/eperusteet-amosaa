package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KayttajaoikeusService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kayttaja")
@Api(value = "kayttajaoikeudet")
@InternalApi
public class KayttajaoikeusController {
    @Autowired
    private KayttajaoikeusService service;

    @RequestMapping(value = "/oikeudet", method = RequestMethod.GET)
    public List<KayttajaoikeusDto> getTyoryhmat() {
        return service.getKayttooikeudet();
    }

    @RequestMapping(value = "/organisaatiooikeudet", method = RequestMethod.GET)
    public ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<Long>>> getOikeudet(
            @RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return service.getOrganisaatiooikeudet(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/koulutustoimijaoikeudet", method = RequestMethod.GET)
    public ResponseEntity<Map<PermissionEvaluator.RolePermission, Set<KoulutustoimijaBaseDto>>> getKoulutustoimijaOikeudet(
            @RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return service.getKoulutustoimijaOikeudet(PermissionEvaluator.RolePrefix.valueOf(app));
    }
}

package fi.vm.sade.eperusteet.amosaa.resource.util;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/kayttaja")
@Tag(name = "kayttaja")
public class KayttajaController {

    @Lazy
    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @RequestMapping(method = RequestMethod.GET)
    public KayttajanTietoDto get() {
        return kayttajanTietoService.haeKirjautaunutKayttaja();
    }

    @RequestMapping(value = "/{oid}", method = RequestMethod.POST)
    public KayttajaDto getOrSaveKayttaja(@PathVariable final String oid) {
        return kayttajanTietoService.saveKayttaja(oid);
    }

    @RequestMapping(value = "/tiedot", method = RequestMethod.GET)
    public KayttajaDto getKayttaja() {
        return kayttajanTietoService.haeKayttajanTiedot();
    }

    @RequestMapping(value = "/suosikki/{opsId}", method = RequestMethod.POST)
    public ResponseEntity addSuosikki(@PathVariable final Long opsId) {
        kayttajanTietoService.addSuosikki(opsId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/suosikki/{opsId}", method = RequestMethod.DELETE)
    public ResponseEntity removeSuosikki(@PathVariable final Long opsId) {
        kayttajanTietoService.removeSuosikki(opsId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.GET)
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat(
            @RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajanTietoService.koulutustoimijat(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.POST)
    public ResponseEntity updateKoulutustoimijat(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajanTietoService.updateKoulutustoimijat(PermissionEvaluator.RolePrefix.valueOf(app))
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/organisaatiot", method = RequestMethod.GET)
    public Set<String> getOrganisaatiot(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajanTietoService.getUserOrganizations(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public EtusivuDto getKayttajanEtusivu(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajanTietoService.haeKayttajanEtusivu(PermissionEvaluator.RolePrefix.valueOf(app));
    }
}

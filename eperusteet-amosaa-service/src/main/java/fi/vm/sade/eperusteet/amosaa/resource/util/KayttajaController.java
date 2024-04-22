package fi.vm.sade.eperusteet.amosaa.resource.util;

import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/kayttaja")
@Api(value = "kayttaja")
@ApiIgnore
public class KayttajaController {

    @Autowired
    private KayttajanTietoService kayttajat;

    @RequestMapping(method = RequestMethod.GET)
    public KayttajanTietoDto get() {
        return kayttajat.haeKirjautaunutKayttaja();
    }

    @RequestMapping(value = "/{oid}", method = RequestMethod.POST)
    public KayttajaDto getOrSaveKayttaja(@PathVariable final String oid) {
        return kayttajat.saveKayttaja(oid);
    }

    @RequestMapping(value = "/tiedot", method = RequestMethod.GET)
    public KayttajaDto getKayttaja() {
        return kayttajat.haeKayttajanTiedot();
    }

    @RequestMapping(value = "/suosikki/{opsId}", method = RequestMethod.POST)
    public ResponseEntity addSuosikki(@PathVariable final Long opsId) {
        kayttajat.addSuosikki(opsId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/suosikki/{opsId}", method = RequestMethod.DELETE)
    public ResponseEntity removeSuosikki(@PathVariable final Long opsId) {
        kayttajat.removeSuosikki(opsId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.GET)
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat(
            @RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.koulutustoimijat(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/koulutustoimijat", method = RequestMethod.POST)
    public ResponseEntity updateKoulutustoimijat(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.updateKoulutustoimijat(PermissionEvaluator.RolePrefix.valueOf(app))
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/organisaatiot", method = RequestMethod.GET)
    public Set<String> getOrganisaatiot(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.getUserOrganizations(PermissionEvaluator.RolePrefix.valueOf(app));
    }

    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public EtusivuDto getKayttajanEtusivu(@RequestParam(value = "app", required = false, defaultValue="ROLE_APP_EPERUSTEET_AMOSAA") final String app) {
        return kayttajat.haeKayttajanEtusivu(PermissionEvaluator.RolePrefix.valueOf(app));
    }
}

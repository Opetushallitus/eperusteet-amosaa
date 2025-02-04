package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaKtoDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SisaltoViiteSijaintiDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHierarkiaDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViitePaikallinenIntegrationDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/koulutustoimijat")
@Tag(name = "koulutustoimijat")
@InternalApi
public class KoulutustoimijaController extends KoulutustoimijaIdGetterAbstractController {

    @Lazy
    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Lazy
    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Lazy
    @Autowired
    private OrganisaatioService organisaatioService;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}", method = RequestMethod.GET)
    public KoulutustoimijaDto getKoulutustoimija(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getKoulutustoimija(ktId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}", method = RequestMethod.PUT)
    public KoulutustoimijaDto updateKoulutustoimija(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody final KoulutustoimijaDto kt
    ) {
        return koulutustoimijaService.updateKoulutustoimija(ktId, kt);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/hylkaa/{vierasKtId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hylkaaYhteistyopyynto(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long vierasKtId
    ) {
        koulutustoimijaService.hylkaaYhteistyopyynto(ktId, vierasKtId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/tutkinnonosat", method = RequestMethod.GET)
    public List<SisaltoViitePaikallinenIntegrationDto> getPaikallisetTutkinnonosat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getPaikallisetTutkinnonOsat(ktId, SisaltoViitePaikallinenIntegrationDto.class);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/kayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajaKtoDto>> getKoulutustoimijaKayttajat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "app") final String app
    ) {
        return new ResponseEntity<>(kayttajanTietoService.getKayttajat(ktId, PermissionEvaluator.RolePrefix.valueOf(app)), HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/kaikkiKayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajaKtoDto>> getKaikkiKayttajat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "app") final String app
    ) {
        return new ResponseEntity<>(kayttajanTietoService.getKaikkiKayttajat(ktId, PermissionEvaluator.RolePrefix.valueOf(app)), HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/ystavaOrganisaatioKayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajaKtoDto>> getYstavaOrganisaatioKayttajat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "app") final String app
    ) {
        return new ResponseEntity<>(kayttajanTietoService.getYstavaOrganisaatioKayttajat(ktId, PermissionEvaluator.RolePrefix.valueOf(app)), HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/kayttajat/{kayttajaOid}", method = RequestMethod.GET)
    public ResponseEntity<KayttajanTietoDto> getKoulutustoimijaKayttaja(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String kayttajaOid
    ) {
        return ResponseEntity.ok(kayttajanTietoService.getKayttaja(ktId, kayttajaOid));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getKoulutustoimijaByKoodi(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String koodi
    ) {
        return new ResponseEntity<>(sisaltoViiteService.getByKoodi(ktId, koodi, SisaltoViiteSijaintiDto.class), HttpStatus.OK);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/yhteistyo", method = RequestMethod.GET)
    public List<KoulutustoimijaBaseDto> getYhteistyoKoulutustoimijat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getYhteistyoKoulutustoimijat(ktId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/hierarkia", method = RequestMethod.GET)
    public OrganisaatioHierarkiaDto getHierarkia(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getOrganisaatioHierarkia(ktId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/ystavat", method = RequestMethod.GET)
    public List<KoulutustoimijaYstavaDto> getOmatYstavat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getOmatYstavat(ktId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/ystavapyynnot", method = RequestMethod.GET)
    public List<KoulutustoimijaBaseDto> getPyynnot(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getPyynnot(ktId);
    }

    @Parameters({
        @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/historialiitokset", method = RequestMethod.GET)
    public List<OrganisaatioHistoriaLiitosDto> getOrganisaatioHistoriaLiitokset(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getOrganisaatioHierarkiaHistoriaLiitokset(ktId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{ktId}/etusivu", method = RequestMethod.GET)
    public EtusivuDto getEtusivu(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "koulutustyypit") final Set<String> koulutustyypit
    ) {
        return koulutustoimijaService.getEtusivu(ktId, koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/koulutuksenjarjestajat", method = RequestMethod.GET)
    public List<KoulutustoimijaDto> getKoulutuksenJarjestajat() {
        return organisaatioService.getKoulutustoimijaOrganisaatiot();
    }
}

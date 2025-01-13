package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuInternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaListausDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.VanhentunutPohjaperusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliTunnisteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat")
@Tag(name = "opetussuunnitelmat")
@InternalApi
public class OpetussuunnitelmaController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private OpetussuunnitelmaService service;

    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private PoistettuService poistetutService;

    @Autowired
    private SisaltoViiteService sisaltoviiteService;

    @Autowired
    private EperusteetService eperusteetService;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = RequestMethod.GET)
    public Page<OpetussuunnitelmaBaseDto> getAllOpetussuunnitelmatBaseSivutettu(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @Parameter(hidden = true) OpsHakuDto query
    ) {
        PageRequest pageRequest = PageRequest.of(query.getSivu(), Math.min(query.getSivukoko(), 25));
        return service.getOpetussuunnitelmat(ktId, pageRequest, query);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Page<OpetussuunnitelmaDto> getAllOpetussuunnitelmatSivutettu(
            @ModelAttribute("solvedKtId") final Long ktId,
            @Parameter(hidden = true) OpsHakuDto query
    ) {
        PageRequest pageRequest = PageRequest.of(query.getSivu(), Math.min(query.getSivukoko(), 25));
        return service.getOpetussuunnitelmat(ktId, pageRequest, query, OpetussuunnitelmaDto.class);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tilastot", method = RequestMethod.GET)
    public List<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot(
            @RequestParam(value = "koulutustyypit", required = true) final Set<String> koulutustyypit
    ) {
        return service.getOpetussuunnitelmaTilastot(koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/vanhentuneet", method = RequestMethod.GET)
    public List<VanhentunutPohjaperusteDto> getPaivitettavatOpetussuunnitelmat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return service.haePaivitystaVaativatPerusteet(ktId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/vanhentuneet/{opsId}", method = RequestMethod.GET)
    public VanhentunutPohjaperusteDto getPaivitettavaOpetussuunnitelma(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.haePaivitystaVaativaPeruste(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/paivita", method = RequestMethod.POST)
    public void paivitaOpetussuunnitelmanPeruste(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        service.paivitaPeruste(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/ystavien", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getAllOtherOrgsOpetussuunnitelmat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "koulutustyypit", required = false) final Set<String> koulutustyypit
    ) {
        return service.getOtherOpetussuunnitelmat(ktId, koulutustyypit != null ? koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()) : null);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = RequestMethod.POST)
    public OpetussuunnitelmaBaseDto addOpetussuunnitelma(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody OpetussuunnitelmaLuontiDto opsDto
    ) {
        return service.addOpetussuunnitelma(ktId, opsDto);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaDto getOpetussuunnitelma(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOpetussuunnitelma(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/kevyt", method = RequestMethod.GET)
    public OpetussuunnitelmaBaseDto getOpetussuunnitelmaKevyt(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOpetussuunnitelma(ktId, opsId, OpetussuunnitelmaBaseDto.class);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/pohja/kevyt", method = RequestMethod.GET)
    public OpetussuunnitelmaBaseDto getOpetussuunnitelmaPohjaKevyt(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOpetussuunnitelmaPohjaKevyt(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/peruste", method = RequestMethod.GET)
    public JsonNode getOpetussuunnitelmaPeruste(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOpetussuunnitelmanPeruste(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}", method = RequestMethod.PUT)
    public OpetussuunnitelmaDto updateOpetussuunnitelma(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody(required = false) OpetussuunnitelmaDto body
    ) {
        return service.update(ktId, opsId, body);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/poistetut", method = RequestMethod.GET)
    public List<PoistettuDto> getPoistetutSisaltoviitteet(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return poistetutService.poistetut(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/poistetut/{poistettuId}/palauta", method = RequestMethod.POST)
    public SisaltoViiteDto palautaOpetussuunnitelmaSisaltoviite(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long poistettuId
    ) {
        return sisaltoviiteService.restoreSisaltoViite(ktId, opsId, poistettuId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/oikeudet", method = RequestMethod.GET)
    public List<KayttajaoikeusDto> getOpetussuunnitelmaOikeudet(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getOikeudet(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/oikeudet/{kayttajaId}", method = RequestMethod.POST)
    public KayttajaoikeusDto updateOpetussuunnitelmaOikeus(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long kayttajaId,
            @RequestBody(required = false) KayttajaoikeusDto body
    ) {
        return service.updateOikeus(ktId, opsId, kayttajaId, body);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    Revision getOpetussuunnitelmaLatestRevision(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getLatestRevision(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/validoi", method = RequestMethod.GET)
    @InternalApi
    public List<Validointi> validoiOpetussuunnitelma(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.validoi(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/versiot", method = RequestMethod.GET)
    @InternalApi
    public List<Revision> getOpetussuunnitelmaRevisions(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getRevisions(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    public Object getOpetussuunnitelmaRevision(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Integer revId
    ) {
        return service.getData(ktId, opsId, revId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/version/{revId}", method = RequestMethod.POST)
    @InternalApi
    public Object revertOpetussuunnitelmaToRevision(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Integer revId
    ) {
        return service.revertTo(ktId, opsId, revId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/tila/{tila}", method = RequestMethod.POST)
    public OpetussuunnitelmaBaseDto updateOpetussuunnitelmaTila(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Tila tila
    ) {
        return service.updateTila(ktId, opsId, tila, true);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/siirra", method = RequestMethod.POST)
    public OpetussuunnitelmaDto updateOpetussuunnitelmaKoulutustoimija(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody(required = false) KoulutustoimijaBaseDto body
    ) {
        return service.updateKoulutustoimija(ktId, opsId, body);
    }


    @Parameters({
        @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/siirraPassivoidusta", method = RequestMethod.POST)
    public OpetussuunnitelmaDto updateOpetussuunnitelmaKoulutustoimijaPassivoidusta(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.updateKoulutustoimijaPassivoidusta(ktId, opsId);
    }

    @Parameters({
        @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/organisaatio/{organisaatioid}", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmatOrganisaatioista(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String organisaatioid
    ) {
        return service.getOpetussuunnitelmatOrganisaatioista(organisaatioid);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/suorituspolku", method = RequestMethod.GET)
    public List<RakenneModuuliTunnisteDto> getPerusteRakenneNakyvat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable Long opsId) {
        return eperusteetService.getSuoritustavat(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/navigaatio", method = GET)
    public NavigationNodeDto getOpetussuunnitelmaNavigation(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.buildNavigation(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/navigaatio/julkinen", method = GET)
    public NavigationNodeDto getOpetussuunnitelmaNavigationJulkinen(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId) {
        return service.buildNavigationJulkinen(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/{opsId}/navigaatio/public", method = GET)
    public NavigationNodeDto getOpetussuunnitelmaNavigationPublic(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestParam(value = "esikatselu", required = false) final boolean esikatselu) {
        return service.buildNavigationPublic(ktId, opsId, esikatselu);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/koulutustoimija", method = RequestMethod.GET)
    public List<OpetussuunnitelmaDto> getKoulutustoimijaOpetussuunnitelmat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "koulutustyypit", required = false) final Set<String> koulutustyypit,
            @RequestParam(value = "tyyppi", required = false) final OpsTyyppi tyyppi
    ) {
        if (CollectionUtils.isNotEmpty(koulutustyypit) && !ObjectUtils.isEmpty(tyyppi)) {
            return service.getOpetussuunnitelmat(ktId, koulutustyypit, tyyppi);
        } else if (CollectionUtils.isNotEmpty(koulutustyypit)) {
            return service.getOpetussuunnitelmat(ktId, koulutustyypit);
        } else if (!ObjectUtils.isEmpty(tyyppi)) {
            return service.getOpetussuunnitelmat(ktId, tyyppi);
        }

        return service.getOpetussuunnitelmat(ktId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/kaikki", method = RequestMethod.GET)
    public Page<OpetussuunnitelmaListausDto> getKaikkiOpetussuunnitelmat(
            @ModelAttribute("solvedKtId") final Long ktId,
            @Parameter(hidden = true) OpsHakuInternalDto query
    ) {
        return service.getOpetussuunnitelmat(ktId, query);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/pohjat", method = RequestMethod.GET)
    public List<OpetussuunnitelmaBaseDto> getOpsPohjat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "koulutustyypit") final Set<String> koulutustyypit,
            @RequestParam(value = "tilat") final Set<String> tilat,
            @RequestParam(value = "tyyppi") final String tyyppi) {
        return service.getPohjat(ktId, tilat.stream().map(Tila::of).collect(Collectors.toSet()), koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toSet()), OpsTyyppi.of(tyyppi));
    }
}

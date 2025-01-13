package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionDto;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteRakenneDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteTutkinnonosaKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}")
@Tag(name = "Sisaltoviitteet")
@InternalApi
public class SisaltoViiteController extends KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private SisaltoViiteService service;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.GET)
    public SisaltoViiteDto.Matala getSisaltoviiteTekstit(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return service.getSisaltoViite(ktId, opsId, svId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
    public List<SisaltoViiteKevytDto> getOtsikot(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/suorituspolut", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getSuorituspolut(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getSuorituspolut(ktId, opsId, SisaltoViiteDto.class);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tutkinnonosat", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getTutkinnonosat(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getTutkinnonOsaViitteet(ktId, opsId, SisaltoViiteDto.class);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tutkinnonosat/kevyt", method = RequestMethod.GET)
    public List<SisaltoViiteTutkinnonosaKevytDto> getTutkinnonosatKevyt(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getTutkinnonOsaViitteet(ktId, opsId, SisaltoViiteTutkinnonosaKevytDto.class);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/sisaltoviitteet/{tyyppi}", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getSisaltoviitteeTyypilla(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final String tyyppi
    ) {
        return service.getSisaltoviitteet(ktId, opsId, SisaltoTyyppi.of(tyyppi));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/pohjansisaltoviitteet/{tyyppi}", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getOpetussuunnitelmanPohjanSisaltoviitteet(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final String tyyppi
    ) {
        return service.getOpetussuunnitelmanPohjanSisaltoviitteet(ktId, opsId, SisaltoTyyppi.of(tyyppi));
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.POST)
    public SisaltoViiteDto.Matala addTekstiKappaleLapsi(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody(required = false) SisaltoViiteDto.Matala tekstiKappaleViiteDto
    ) {
        tekstiKappaleViiteDto.setLapset(new ArrayList<>());
        return service.addSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void copyMultipleSisaltoviite(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody List<Long> viitteet
    ) {
        service.copySisaltoViiteet(ktId, opsId, viitteet);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/linkkaa", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkkaaUusiSisalto(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody List<Long> viitteet
    ) {
        service.linkSisaltoViiteet(ktId, opsId, viitteet);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/kopioiLinkattu", method = RequestMethod.POST)
    public SisaltoViiteDto kopioiLinkattuSisalto(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody Long viiteId
    ) {
        return service.kopioiLinkattuSisaltoViiteet(ktId, opsId, viiteId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.PUT)
    public void updateTekstiKappaleViite(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteDto tekstiKappaleViiteDto
    ) {
        service.updateSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}/rakenne", method = RequestMethod.PUT)
    public void updateSisaltoViiteRakenne(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteRakenneDto rakenneDto
    ) {
        service.reorderSubTree(ktId, opsId, svId, rakenneDto);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSisaltoViite(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        service.removeSisaltoViite(ktId, opsId, svId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/removeViitteet", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeSisaltoViitteet(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody List<Long> viitteet
    ) {
        service.removeSisaltoViitteet(ktId, opsId, viitteet);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    RevisionDto getLatestRevision(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return service.getLatestRevision(ktId, opsId, svId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}/versiot", method = RequestMethod.GET)
    @InternalApi
    public List<RevisionKayttajaDto> getSisaltoviiteRevisions(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return service.getRevisions(ktId, opsId, svId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/tekstit/{svId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    public SisaltoViiteDto getSisaltoviiteRevision(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @PathVariable final Integer revId
    ) {
        return service.getData(ktId, opsId, svId, revId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/polut", method = RequestMethod.GET)
    public List<SuorituspolkuRakenneDto> getSuorituspolutRakenteella(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getSuorituspolkurakenne(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(value = "/sisaltoviitteet", method = RequestMethod.GET)
    public Page<SisaltoviiteLaajaDto> getSisaltoviitteet(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @Parameter(hidden = true) SisaltoviiteQueryDto query
    ) {
        return service.getSisaltoviitteetWithQuery(ktId, query, SisaltoviiteLaajaDto.class);
    }
}

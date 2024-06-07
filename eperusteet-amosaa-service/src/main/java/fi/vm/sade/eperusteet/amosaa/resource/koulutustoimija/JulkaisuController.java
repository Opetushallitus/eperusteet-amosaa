package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuTila;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}")
@Tag(name = "Julkaisut")
@Description("Opetussuunnitelmien julkaisut")
public class JulkaisuController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private JulkaisuService julkaisutService;

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = GET, value = "/julkaisut")
    public List<JulkaisuBaseDto> getJulkaisut(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId) {
        return julkaisutService.getJulkaisutJaViimeisinStatus(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = GET, value = "/julkaisut/kaikki")
    public List<JulkaisuBaseDto> getJulkaisutKaikki(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId) {
        return julkaisutService.getJulkaisut(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = POST, value = "/julkaisu")
    public void teeJulkaisu(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId,
            @RequestBody JulkaisuBaseDto julkaisuBaseDto) {
        julkaisutService.teeJulkaisu(ktId, opsId, julkaisuBaseDto);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = POST, value = "/aktivoi/{revision}")
    public JulkaisuBaseDto aktivoiJulkaisu(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId,
            @PathVariable("revision") final int revision) {
        return julkaisutService.aktivoiJulkaisu(ktId, opsId, revision);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = GET, value = "/julkaisu/muutoksia")
    public boolean julkaisemattomiaMuutoksia(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId) {
        return julkaisutService.julkaisemattomiaMuutoksia(ktId, opsId);
    }

    @Parameters({
            @Parameter(name = "ktId", schema = @Schema(implementation = String.class), in = ParameterIn.PATH)
    })
    @RequestMapping(method = GET, value = "/viimeisinjulkaisutila")
    public JulkaisuTila viimeisinJulkaisuTila(
            @Parameter(hidden = true) @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId) {
        return julkaisutService.viimeisinJulkaisuTila(ktId, opsId);
    }

}

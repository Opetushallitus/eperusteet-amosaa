package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}")
@Api(value = "Julkaisut")
@Description("Opetussuunnitelmien julkaisut")
public class JulkaisuController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private JulkaisuService julkaisutService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = GET, value = "/julkaisut")
    public List<JulkaisuBaseDto> getJulkaisut(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId) {
        return julkaisutService.getJulkaisut(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = POST, value = "/julkaisu")
    public JulkaisuBaseDto teeJulkaisu(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId,
            @RequestBody JulkaisuBaseDto julkaisuBaseDto) {
        return julkaisutService.teeJulkaisu(ktId, opsId, julkaisuBaseDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = POST, value = "/aktivoi/{revision}")
    public JulkaisuBaseDto aktivoiJulkaisu(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId,
            @PathVariable("revision") final int revision) {
        return julkaisutService.aktivoiJulkaisu(ktId, opsId, revision);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(method = GET, value = "/julkaisu/onkoMuutoksia")
    public boolean onkoMuutoksia(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable("opsId") final long opsId) {
        return julkaisutService.onkoMuutoksia(ktId, opsId);
    }

}

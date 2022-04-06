package fi.vm.sade.eperusteet.amosaa.resource.julkinen;

import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/external", produces = "application/json;charset=UTF-8")
@Api(value = "Julkinen")
@Description("Opetussuunnitelminen julkinen rajapinta")
public class ExternalController {

    @Autowired
    private OpetussuunnitelmaService opsService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteId", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "organisaatio", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tyyppi", dataType = "string", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "organisaatioRyhma", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "oppilaitosTyyppiKoodiUri", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "koulutustyyppi", dataType = "string", paramType = "query", allowMultiple = true, required = true),
    })
    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    @Description("Opetussuunnitelmien haku.")
    public Page<OpetussuunnitelmaDto> getPublicOpetussuunnitelmat(
            @ApiIgnore final OpetussuunnitelmaJulkaistuQueryDto pquery
    ) {
        return opsService.findOpetussuunnitelmatJulkaisut(pquery);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "opetussuunnitelma/{koulutustoimijaId}/{opsId}", method = RequestMethod.GET)
    public OpetussuunnitelmaKaikkiDto getPublicOpetussuunnitelma(
            @ApiIgnore @ModelAttribute("koulutustoimijaId") final Long koulutustoimijaId,
            @PathVariable final Long opsId
    ) {
        return opsService.getOpetussuunnitelmaJulkaistuSisalto(koulutustoimijaId, opsId);
    }
}

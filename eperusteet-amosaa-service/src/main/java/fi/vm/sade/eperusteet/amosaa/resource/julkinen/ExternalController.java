package fi.vm.sade.eperusteet.amosaa.resource.julkinen;

import fi.vm.sade.eperusteet.amosaa.dto.external.SisaltoviiteOpintokokonaisuusExternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/api/external", produces = "application/json;charset=UTF-8")
@Api(value = "external")
@Description("Opetussuunnitelminen julkinen rajapinta")
public class ExternalController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    private static final int DEFAULT_PATH_SKIP_VALUE = 5;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteId", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "organisaatio", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "tyyppi", dataType = "string", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "oppilaitosTyyppiKoodiUri", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "koulutustyyppi", dataType = "string", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "tuleva", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "voimassaolo", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "poistunut", dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "jotpatyyppi", dataType = "string", paramType = "query", allowMultiple = true),
    })
    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    @Description("Opetussuunnitelmien haku.")
    public Page<OpetussuunnitelmaDto> getPublicOpetussuunnitelmat(
            @ApiIgnore final OpetussuunnitelmaJulkaistuQueryDto pquery
    ) {
        return opetussuunnitelmaService.findOpetussuunnitelmatJulkaisut(pquery);
    }

    @ApiOperation(value = "Opetussuunnitelman tietojen haku")
    @RequestMapping(value = "/opetussuunnitelma/{opsId:\\d+}", method = RequestMethod.GET)
    public OpetussuunnitelmaKaikkiDto getPublicOpetussuunnitelma(
            @PathVariable final Long opsId
    ) {
        return opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId);
    }
    
    @RequestMapping(value = "/opetussuunnitelma/{koulutustoimijaId:\\d+}/{opsId:\\d+}", method = RequestMethod.GET)
    @ApiOperation(value = "Opetussuunnitelman tietojen haku koulutustoimijan ja opetussuunnitelman id:llä")
    public OpetussuunnitelmaKaikkiDto getPublicOpetussuunnitelmaKoulutustoimija(
            @PathVariable final Long koulutustoimijaId,
            @PathVariable final Long opsId
    ) {
        return opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId);
    }

    @RequestMapping(value = "/opetussuunnitelma/{opsId:\\d+}/**", method = GET)
    @ResponseBody
    @ApiOperation(
            value = "Opetussuunnitelman tietojen haku tarkalla sisältörakenteella",
            notes = "Url parametreiksi voi antaa opetussuunnitelman id:n lisäksi erilaisia opetussuunnitelman rakenteen osia ja id-kenttien arvoja. Esim. /opetussuunnitelma/8505691/tutkinnonOsat/7283253/tosa antaa opetussuunnitelman (id: 8505691) tutkinnon osien tietueen (id: 7283253).",
            response= OpetussuunnitelmaKaikkiDto.class
    )
    public ResponseEntity<Object> getOpetussuunnitelmaDynamicQuery(HttpServletRequest req, @PathVariable("opsId") final long id) {
        return getJulkaistuSisaltoObjectNodeWithQuery(id, requestToQueries(req, DEFAULT_PATH_SKIP_VALUE));
    }

    @ApiOperation(value = "Opintokokonaisuuden haku opintokokonaisuuden koodin arvolla")
    @RequestMapping(value = "/opintokokonaisuus/{koodiArvo}", method = RequestMethod.GET)
    public ResponseEntity<SisaltoviiteOpintokokonaisuusExternalDto> getPublicOpintokokonaisuusKoodilla(@PathVariable final String koodiArvo) throws IOException {
        return ResponseEntity.of(Optional.ofNullable(opetussuunnitelmaService.findJulkaistuOpintokokonaisuus(koodiArvo)));
    }

    private List<String> requestToQueries(HttpServletRequest req, int skipCount) {
        String[] queries = req.getServletPath().split("/");
        return Arrays.stream(queries).skip(skipCount).collect(Collectors.toList());
    }

    private ResponseEntity<Object> getJulkaistuSisaltoObjectNodeWithQuery(long id, List<String> queries) {
        Object result = opetussuunnitelmaService.getJulkaistuSisaltoObjectNode(id, queries);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }
}

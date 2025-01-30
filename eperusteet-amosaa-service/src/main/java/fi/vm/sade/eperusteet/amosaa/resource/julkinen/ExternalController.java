package fi.vm.sade.eperusteet.amosaa.resource.julkinen;

import fi.vm.sade.eperusteet.amosaa.dto.external.SisaltoviiteOpintokokonaisuusExternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/external", produces = "application/json;charset=UTF-8")
@Tag(name = "external")
@Description("Opetussuunnitelminen julkinen rajapinta")
public class ExternalController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    private static final int DEFAULT_PATH_SKIP_VALUE = 5;

    @Parameters({
            @Parameter(name = "perusteenDiaarinumero", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
            @Parameter(name = "perusteId", schema = @Schema(implementation = Long.class), in = ParameterIn.QUERY),
            @Parameter(name = "organisaatio", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
            @Parameter(name = "tyyppi", in = ParameterIn.QUERY, array =  @ArraySchema(schema = @Schema(type = "string"))),
            @Parameter(name = "sivu", schema = @Schema(implementation = Long.class, defaultValue = "false"), in = ParameterIn.QUERY, description = "Sivunumero (0 perusarvo)"),
            @Parameter(name = "sivukoko", schema = @Schema(implementation = Long.class), in = ParameterIn.QUERY),
            @Parameter(name = "nimi", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
            @Parameter(name = "kieli", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
            @Parameter(name = "oppilaitosTyyppiKoodiUri", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
            @Parameter(name = "koulutustyyppi", in = ParameterIn.QUERY, array =  @ArraySchema(schema = @Schema(type = "string"))),
            @Parameter(name = "tuleva", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY),
            @Parameter(name = "voimassaolo", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY),
            @Parameter(name = "poistunut", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY),
            @Parameter(name = "jotpatyyppi", in = ParameterIn.QUERY, array =  @ArraySchema(schema = @Schema(type = "string"))),
    })
    @RequestMapping(value = "/opetussuunnitelmat", method = RequestMethod.GET)
    @Description("Opetussuunnitelmien haku.")
    public Page<OpetussuunnitelmaDto> getPublicOpetussuunnitelmat(
            @Parameter(hidden = true) final OpetussuunnitelmaJulkaistuQueryDto pquery
    ) {
        return opetussuunnitelmaService.findOpetussuunnitelmatJulkaisut(pquery);
    }

    @Operation(summary = "Opetussuunnitelman tietojen haku")
    @RequestMapping(value = "/opetussuunnitelma/{opsId:\\d+}", method = RequestMethod.GET)
    public OpetussuunnitelmaKaikkiDto getPublicOpetussuunnitelma(
            @PathVariable final Long opsId
    ) {
        return opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId);
    }
    
    @RequestMapping(value = "/opetussuunnitelma/{koulutustoimijaId:\\d+}/{opsId:\\d+}", method = RequestMethod.GET)
    @Operation(summary = "Opetussuunnitelman tietojen haku koulutustoimijan ja opetussuunnitelman id:llä")
    public OpetussuunnitelmaKaikkiDto getPublicOpetussuunnitelmaKoulutustoimija(
            @PathVariable final Long koulutustoimijaId,
            @PathVariable final Long opsId
    ) {
        return opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId);
    }

    @RequestMapping(value = "/opetussuunnitelma/{opsId:\\d+}/{custompath}", method = GET)
    @ResponseBody
    @Operation(
            summary = "Opetussuunnitelman tietojen haku tarkalla sisältörakenteella",
            description = "Url parametreiksi voi antaa opetussuunnitelman id:n lisäksi erilaisia opetussuunnitelman rakenteen osia ja id-kenttien arvoja. Esim. /opetussuunnitelma/8505691/tutkinnonOsat/7283253/tosa antaa opetussuunnitelman (id: 8505691) tutkinnon osien tietueen (id: 7283253)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OpetussuunnitelmaKaikkiDto.class))}),
    })
    public ResponseEntity<Object> getOpetussuunnitelmaDynamicQuery(HttpServletRequest req, @PathVariable("opsId") final long id) {
        return getJulkaistuSisaltoObjectNodeWithQuery(id, requestToQueries(req, DEFAULT_PATH_SKIP_VALUE));
    }

    // Springdoc ei generoi rajapintoja /** poluille, joten tämä on tehty erikseen
    @RequestMapping(value = "/opetussuunnitelma/{opsId:\\d+}/{custompath}/**", method = GET)
    public ResponseEntity<Object> getOpetussuunnitelmaDynamicQueryHidden(HttpServletRequest req, @PathVariable("opsId") final long id) {
        return getJulkaistuSisaltoObjectNodeWithQuery(id, requestToQueries(req, DEFAULT_PATH_SKIP_VALUE));
    }

    @Operation(summary = "Opintokokonaisuuden haku opintokokonaisuuden koodin arvolla")
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

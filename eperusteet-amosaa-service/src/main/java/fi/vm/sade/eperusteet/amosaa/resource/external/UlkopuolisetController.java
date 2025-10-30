package fi.vm.sade.eperusteet.amosaa.resource.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.external.KoodistoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/ulkopuoliset")
@InternalApi
@Tag(name = "Ulkopuoliset")
public class UlkopuolisetController {

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private EperusteetClient eperusteetClient;
    
    @Lazy
    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @RequestMapping(value = "/kayttajatiedot/{oid:.+}", method = GET)
    public ResponseEntity<KayttajanTietoDto> getKayttajatiedot(@PathVariable final String oid) {
        return new ResponseEntity<>(kayttajanTietoService.hae(oid), HttpStatus.OK);
    }

    @RequestMapping(value = "/perusteet", method = GET)
    public ResponseEntity<List<PerusteKevytDto>> getPerusteet(
            @RequestParam(value = "koulutustyyppi", required = false) final Set<String> koulutustyypit,
            @RequestParam(value = "nimi", required = false, defaultValue = "") final String nimi,
            @RequestParam(value = "kieli", required = false, defaultValue = "fi") final String kieli) {
        return new ResponseEntity<>(eperusteetService.findPerusteet(koulutustyypit, nimi, kieli), HttpStatus.OK);
    }

    @RequestMapping(value = "/tiedotteet", method = GET)
    public ResponseEntity<JsonNode> getTiedotteet(@RequestParam(value = "jalkeen", required = false) final Long jalkeen) {
        return new ResponseEntity<>(eperusteetService.getTiedotteet(jalkeen), HttpStatus.OK);
    }

    @RequestMapping(value = "/geneerisetArvioinnit", method = GET)
    public ResponseEntity<JsonNode> getGeneerisetArvioinnit() {
        return new ResponseEntity<>(eperusteetService.getGeneeriset(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tiedotteet/haku", method = GET)
    @ResponseBody
    @Operation(summary = "tiedotteiden haku")
    @Parameters({
            @Parameter(name = "sivu", schema = @Schema(implementation = Long.class), in = ParameterIn.QUERY),
            @Parameter(name = "sivukoko", schema = @Schema(implementation = Long.class), in = ParameterIn.QUERY),
            @Parameter(name = "kieli", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "tiedotteen kieli"),
            @Parameter(name = "nimi", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "hae nimellä"),
            @Parameter(name = "perusteId", schema = @Schema(implementation = Long.class), in = ParameterIn.QUERY, description = "hae perusteeseen liitetyt tiedotteet"),
            @Parameter(name = "perusteeton", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "hae perusteettomat tiedotteet"),
            @Parameter(name = "julkinen", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "hae julkiset tiedotteet"),
            @Parameter(name = "yleinen", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "hae yleiset tiedotteet"),
            @Parameter(name = "perusteIds", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "number")), description = "tiedotteen perusteiden"),
            @Parameter(name = "tiedoteJulkaisuPaikka", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "tiedotteen julkaisupaikat"),
            @Parameter(name = "koulutusTyyppi", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "tiedotteen koulutustyypit"),
            @Parameter(name = "jarjestys", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "tiedotteen jarjestys"),
            @Parameter(name = "jarjestysNouseva", schema = @Schema(implementation = Boolean.class), in = ParameterIn.QUERY, description = "tiedotteen jarjestyksen suunta")
    })
    public ResponseEntity<JsonNode> getTiedotteetHaku(@Parameter(hidden = true) TiedoteQueryDto queryDto) {
        return ResponseEntity.ok(eperusteetClient.getTiedotteetHaku(queryDto));
    }

    @RequestMapping(value = "/koodisto/{koodisto}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> kaikki(
            @PathVariable final String koodisto,
            @RequestParam(value = "haku", required = false) final String haku) {
        return new ResponseEntity<>(haku == null || haku.isEmpty()
                ? koodistoService.getAll(koodisto)
                : koodistoService.filterBy(koodisto, haku), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/{koodisto}/{koodi}", method = GET)
    public ResponseEntity<KoodistoKoodiDto> yksi(
            @PathVariable final String koodisto,
            @PathVariable final String koodi) {
        return new ResponseEntity<>(koodistoService.get(koodisto, koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/relaatio/sisaltyy-alakoodit/{koodi}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> alarelaatio(
            @PathVariable final String koodi) {
        return new ResponseEntity<>(koodistoService.getAlarelaatio(koodi), HttpStatus.OK);
    }

    @RequestMapping(value = "/koodisto/relaatio/sisaltyy-ylakoodit/{koodi}", method = GET)
    public ResponseEntity<List<KoodistoKoodiDto>> ylarelaatio(
            @PathVariable final String koodi) {
        return new ResponseEntity<>(koodistoService.getYlarelaatio(koodi), HttpStatus.OK);
    }
}

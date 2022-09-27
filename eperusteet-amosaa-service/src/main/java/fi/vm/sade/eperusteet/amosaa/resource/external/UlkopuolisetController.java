/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author mikkom
 */
@RestController
@RequestMapping("/ulkopuoliset")
@InternalApi
@Api("Ulkopuoliset")
public class UlkopuolisetController {

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private EperusteetClient eperusteetClient;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @RequestMapping(value = "/kayttajatiedot/{oid:.+}", method = GET)
    public ResponseEntity<KayttajanTietoDto> getKayttajatiedot(@PathVariable final String oid) {
        return new ResponseEntity<>(kayttajanTietoService.hae(oid), HttpStatus.OK);
    }

    @RequestMapping(value = "/julkaistutperusteet", method = GET)
    public ResponseEntity<List<PerusteDto>> getJulkaistutPerusteet() {
        return new ResponseEntity<>(eperusteetService.findPerusteet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/julkaistutperusteet/kevyt", method = GET)
    public ResponseEntity<List<PerusteKevytDto>> getJulkaistutPerusteetKevyt(@RequestParam(value = "koulutustyyppi", required = false) final Set<String> koulutustyypit) {
        return new ResponseEntity<>(eperusteetService.findPerusteet(koulutustyypit, PerusteKevytDto.class), HttpStatus.OK);
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
    @ApiOperation(value = "tiedotteiden haku")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query", allowMultiple = true, value = "tiedotteen kieli"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query", value = "hae nimellä"),
            @ApiImplicitParam(name = "perusteId", dataType = "long", paramType = "query", value = "hae perusteeseen liitetyt tiedotteet"),
            @ApiImplicitParam(name = "perusteeton", dataType = "boolean", paramType = "query", value = "hae perusteettomat tiedotteet"),
            @ApiImplicitParam(name = "julkinen", dataType = "boolean", paramType = "query", value = "hae julkiset tiedotteet"),
            @ApiImplicitParam(name = "yleinen", dataType = "boolean", paramType = "query", value = "hae yleiset tiedotteet"),
            @ApiImplicitParam(name = "perusteIds", dataType = "long", paramType = "query", allowMultiple = true, value = "tiedotteen perusteiden"),
            @ApiImplicitParam(name = "tiedoteJulkaisuPaikka", dataType = "string", paramType = "query", allowMultiple = true, value = "tiedotteen julkaisupaikat"),
            @ApiImplicitParam(name = "koulutusTyyppi", dataType = "string", paramType = "query", allowMultiple = true, value = "tiedotteen koulutustyypit"),
            @ApiImplicitParam(name = "jarjestys", dataType = "string", paramType = "query", allowMultiple = false, value = "tiedotteen jarjestys"),
            @ApiImplicitParam(name = "jarjestysNouseva", dataType = "boolean", paramType = "query", allowMultiple = false, value = "tiedotteen jarjestyksen suunta")
    })
    public ResponseEntity<JsonNode> getTiedotteetHaku(@ApiIgnore TiedoteQueryDto queryDto) {
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

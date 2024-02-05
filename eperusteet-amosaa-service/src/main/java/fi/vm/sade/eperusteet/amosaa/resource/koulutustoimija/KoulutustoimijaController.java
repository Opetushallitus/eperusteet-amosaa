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

package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaDto;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author nkala
 */
@RestController
@RequestMapping("/api/koulutustoimijat")
@Api(value = "koulutustoimijat")
@InternalApi
public class KoulutustoimijaController extends KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private KoulutustoimijaService koulutustoimijaService;

    @Autowired
    private SisaltoViiteService sisaltoViiteService;

    @Autowired
    private KayttajanTietoService kayttajaTietoService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}", method = RequestMethod.GET)
    public KoulutustoimijaDto getKoulutustoimija(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getKoulutustoimija(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}", method = RequestMethod.PUT)
    public KoulutustoimijaDto updateKoulutustoimija(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody final KoulutustoimijaDto kt
    ) {
        return koulutustoimijaService.updateKoulutustoimija(ktId, kt);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/hylkaa/{vierasKtId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hylkaaYhteistyopyynto(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long vierasKtId
    ) {
        koulutustoimijaService.hylkaaYhteistyopyynto(ktId, vierasKtId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/tutkinnonosat", method = RequestMethod.GET)
    public List<SisaltoViitePaikallinenIntegrationDto> getPaikallisetTutkinnonosat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getPaikallisetTutkinnonOsat(ktId, SisaltoViitePaikallinenIntegrationDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/kayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajaKtoDto>> getKoulutustoimijaKayttajat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "app") final String app
    ) {
        return new ResponseEntity<>(kayttajaTietoService.getKayttajat(ktId, PermissionEvaluator.RolePrefix.valueOf(app)), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/kaikkiKayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajaKtoDto>> getKaikkiKayttajat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "app") final String app
    ) {
        return new ResponseEntity<>(kayttajaTietoService.getKaikkiKayttajat(ktId, PermissionEvaluator.RolePrefix.valueOf(app)), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/ystavaOrganisaatioKayttajat", method = RequestMethod.GET)
    public ResponseEntity<List<KayttajaKtoDto>> getYstavaOrganisaatioKayttajat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "app") final String app
    ) {
        return new ResponseEntity<>(kayttajaTietoService.getYstavaOrganisaatioKayttajat(ktId, PermissionEvaluator.RolePrefix.valueOf(app)), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/kayttajat/{kayttajaOid}", method = RequestMethod.GET)
    public ResponseEntity<KayttajanTietoDto> getKoulutustoimijaKayttaja(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String kayttajaOid
    ) {
        return ResponseEntity.ok(kayttajaTietoService.getKayttaja(ktId, kayttajaOid));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/koodi/{koodi}", method = RequestMethod.GET)
    public ResponseEntity<List<SisaltoViiteSijaintiDto>> getKoulutustoimijaByKoodi(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String koodi
    ) {
        return new ResponseEntity<>(sisaltoViiteService.getByKoodi(ktId, koodi, SisaltoViiteSijaintiDto.class), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/yhteistyo", method = RequestMethod.GET)
    public List<KoulutustoimijaBaseDto> getYhteistyoKoulutustoimijat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getYhteistyoKoulutustoimijat(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/hierarkia", method = RequestMethod.GET)
    public OrganisaatioHierarkiaDto getHierarkia(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getOrganisaatioHierarkia(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/ystavat", method = RequestMethod.GET)
    public List<KoulutustoimijaYstavaDto> getOmatYstavat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getOmatYstavat(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/ystavapyynnot", method = RequestMethod.GET)
    public List<KoulutustoimijaBaseDto> getPyynnot(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getPyynnot(ktId);
    }

    @ApiImplicitParams({
        @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/historialiitokset", method = RequestMethod.GET)
    public List<OrganisaatioHistoriaLiitosDto> getOrganisaatioHistoriaLiitokset(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId
    ) {
        return koulutustoimijaService.getOrganisaatioHierarkiaHistoriaLiitokset(ktId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/{ktId}/etusivu", method = RequestMethod.GET)
    public EtusivuDto getEtusivu(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @RequestParam(value = "koulutustyypit") final Set<String> koulutustyypit
    ) {
        return koulutustoimijaService.getEtusivu(ktId, koulutustyypit.stream().map(KoulutusTyyppi::of).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/koulutuksenjarjestajat", method = RequestMethod.GET)
    public List<KoulutustoimijaDto> getKoulutuksenJarjestajat() {
        return organisaatioService.getKoulutustoimijaOrganisaatiot();
    }
}

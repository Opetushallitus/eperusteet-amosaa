package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

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

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionDto;
import fi.vm.sade.eperusteet.amosaa.dto.RevisionKayttajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.SisaltoviiteQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KoulutuksenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteRakenneDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.ArrayList;
import java.util.List;
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
import springfox.documentation.annotations.ApiIgnore;


/**
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}")
@Api(value = "Sisaltoviitteet")
@InternalApi
public class SisaltoViiteController extends KoulutustoimijaIdGetterAbstractController {
    @Autowired
    private SisaltoViiteService service;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.GET)
    public SisaltoViiteDto.Matala getSisaltoviiteTekstit(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return service.getSisaltoViite(ktId, opsId, svId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
    public List<SisaltoViiteKevytDto> getOtsikot(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getSisaltoViitteet(ktId, opsId, SisaltoViiteKevytDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/suorituspolut", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getSuorituspolut(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getSuorituspolut(ktId, opsId, SisaltoViiteDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tutkinnonosat", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getTutkinnonosat(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getTutkinnonOsaViitteet(ktId, opsId, SisaltoViiteDto.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/sisaltoviitteet/{tyyppi}", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getSisaltoviitteeTyypilla(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final String tyyppi
    ) {
        return service.getSisaltoviitteet(ktId, opsId, SisaltoTyyppi.of(tyyppi));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/pohjansisaltoviitteet/{tyyppi}", method = RequestMethod.GET)
    public List<SisaltoViiteDto> getOpetussuunnitelmanPohjanSisaltoviitteet(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final String tyyppi
    ) {
        return service.getOpetussuunnitelmanPohjanSisaltoviitteet(ktId, opsId, SisaltoTyyppi.of(tyyppi));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.POST)
    public SisaltoViiteDto.Matala addTekstiKappaleLapsi(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody(required = false) SisaltoViiteDto.Matala tekstiKappaleViiteDto
    ) {
        tekstiKappaleViiteDto.setLapset(new ArrayList<>());
        return service.addSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/lisaa", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void copyMultipleSisaltoviite(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody List<Long> viitteet
    ) {
        service.copySisaltoViiteet(ktId, opsId, viitteet);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.PUT)
    public void updateTekstiKappaleViite(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteDto tekstiKappaleViiteDto
    ) {
        service.updateSisaltoViite(ktId, opsId, svId, tekstiKappaleViiteDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}/rakenne", method = RequestMethod.PUT)
    public void updateSisaltoViiteRakenne(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @RequestBody final SisaltoViiteRakenneDto rakenneDto
    ) {
        service.reorderSubTree(ktId, opsId, svId, rakenneDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSisaltoViite(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        service.removeSisaltoViite(ktId, opsId, svId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    RevisionDto getLatestRevision(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return service.getLatestRevision(ktId, opsId, svId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}/versiot", method = RequestMethod.GET)
    @InternalApi
    public List<RevisionKayttajaDto> getSisaltoviiteRevisions(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId
    ) {
        return service.getRevisions(ktId, opsId, svId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/tekstit/{svId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    public SisaltoViiteDto getSisaltoviiteRevision(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long svId,
            @PathVariable final Integer revId
    ) {
        return service.getData(ktId, opsId, svId, revId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/polut", method = RequestMethod.GET)
    public List<SuorituspolkuRakenneDto> getSuorituspolutRakenteella(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId
    ) {
        return service.getSuorituspolkurakenne(ktId, opsId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ktId", dataType = "string", paramType = "path")
    })
    @RequestMapping(value = "/sisaltoviitteet", method = RequestMethod.GET)
    public Page<SisaltoviiteLaajaDto> getSisaltoviitteet(
            @ApiIgnore @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long opsId,
            @ApiIgnore SisaltoviiteQueryDto query
    ) {
        return service.getSisaltoviitteetWithQuery(ktId, query, SisaltoviiteLaajaDto.class);
    }
}

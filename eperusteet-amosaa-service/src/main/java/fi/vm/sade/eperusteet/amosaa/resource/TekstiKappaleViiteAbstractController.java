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

package fi.vm.sade.eperusteet.amosaa.resource;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.service.ops.TekstiKappaleViiteService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author nkala
 */
public interface TekstiKappaleViiteAbstractController {

    TekstiKappaleViiteService service();

    @RequestMapping(value = "/tekstit/{tkvId}", method = RequestMethod.GET)
    @ResponseBody
    default public TekstiKappaleViiteDto.Matala getTekstit(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id,
            @PathVariable("tkvId") final Long tkvId) {
        return service().getTekstiKappaleViite(baseId, id, tkvId);
    }

    @RequestMapping(value = "/tekstit/otsikot", method = RequestMethod.GET)
    @ResponseBody
    default public List<TekstiKappaleViiteKevytDto> getOtsikot(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id) {
        return service().getTekstiKappaleViitteet(baseId, id, TekstiKappaleViiteKevytDto.class);
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.POST)
    @ResponseBody
    default public TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id,
            @PathVariable("viiteId") final Long viiteId,
            @RequestBody(required = false) TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto) {
        tekstiKappaleViiteDto.setLapset(new ArrayList<>());
        return service().addTekstiKappaleViite(baseId, id, viiteId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.PUT)
    default public void updateTekstiKappaleViite(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id,
            @PathVariable("viiteId") final Long viiteId,
            @RequestBody final TekstiKappaleViiteDto.Puu tekstiKappaleViiteDto) {
        service().updateTekstiKappaleViite(baseId, id, viiteId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/rakenne", method = RequestMethod.PUT)
    default public void updateTekstiKappaleViiteRakenne(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id,
            @PathVariable("viiteId") final Long viiteId,
            @RequestBody final TekstiKappaleViiteDto.Puu tekstiKappaleViiteDto) {
        service().reorderSubTree(baseId, id, viiteId, tekstiKappaleViiteDto);
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    default public void removeTekstiKappaleViite(
            @PathVariable("baseId") final Long baseId,
            @PathVariable("id") final Long id,
            @PathVariable("viiteId") final Long viiteId) {
        service().removeTekstiKappaleViite(baseId, id, viiteId);
    }

//    @RequestMapping(value = "/tekstit/{viiteId}/versiot", method = GET)
//    public ResponseEntity<List<RevisionDto>> getVersionsForTekstiKappaleViite(
//            @PathVariable("viiteId") final long viiteId) {
//
//        return new ResponseEntity<>(tekstiKappaleViiteService.getVersions(viiteId), HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/tekstit/{viiteId}/versio/{versio}", method = GET)
//    public TekstiKappaleDto getVersionForTekstiKappaleViite(
//            @PathVariable("opsId") final Long opsId,
//            @PathVariable("viiteId") final long viiteId,
//            @PathVariable("versio") final long versio) {
//        return tekstiKappaleViiteService.findTekstikappaleVersion(opsId, viiteId, versio);
//    }
//
//    @RequestMapping(value = "/tekstit/{viiteId}/revert/{versio}", method = RequestMethod.POST)
//    public void revertTekstikappaleToVersion(
//        @PathVariable("opsId") final Long opsId,
//        @PathVariable("viiteId") final Long viiteId,*
//        @PathVariable("versio") final Integer versio){
//        tekstiKappaleViiteService.revertToVersion(opsId, viiteId, versio);
//    }

//    @RequestMapping(value = "/tekstit/{parentId}/lapsi/{childId}", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<TekstiKappaleViiteDto.Matala> addTekstiKappaleLapsi(
//            @PathVariable("opsId") final Long opsId,
//            @PathVariable("parentId") final Long parentId,
//            @PathVariable("childId") final Long childId) {
//        TekstiKappaleViiteDto.Matala viite = new TekstiKappaleViiteDto.Matala();
//        viite.setTekstiKappaleRef(Reference.of(childId));
//        return new ResponseEntity<>(
//                service().addTekstiKappaleLapsi(opsId, parentId, viite), HttpStatus.CREATED);
//    }

//    @RequestMapping(value = "/tekstit", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteDto.Puu> getTekstit(
//            @PathVariable("opsId") final Long opsId) {
//        TekstiKappaleViiteDto.Puu dto = service().getTekstit(opsId, TekstiKappaleViiteDto.Puu.class);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteKevytDto> getTekstiOtsikot(@PathVariable("opsId") final Long opsId) {
//        TekstiKappaleViiteKevytDto dto = service().getTekstit(opsId, TekstiKappaleViiteKevytDto.class);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteDto.Matala> getTekstiKappaleViite(
//            @PathVariable("opsId") final Long opsId,
//            @PathVariable("viiteId") final Long viiteId) {
//        TekstiKappaleViiteDto.Matala dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/tekstit/{viiteId}/kaikki", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteDto.Puu> getTekstiKappaleViiteSyva(
//            @PathVariable("opsId") final Long opsId,
//            @PathVariable("viiteId") final Long viiteId) {
//        TekstiKappaleViiteDto.Puu dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId, TekstiKappaleViiteDto.Puu.class);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }

//    @RequestMapping(value = "/tekstit/{viiteId}/muokattavakopio", method = RequestMeth*od.POST)
//    public TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(
//            @PathVariable("opsId") final Long opsId,
//            @PathVariable("viiteId") final Long viiteId) {
//        return tekstiKappaleViiteService.kloonaaTekstiKappale(opsId, viiteId);
//    }
}

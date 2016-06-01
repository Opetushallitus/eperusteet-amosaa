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

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.util.PoistettuService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nkala
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat")
@Api(value = "opetussuunnitelmat")
public class OpetussuunnitelmaController {

    @Autowired
    private OpetussuunnitelmaService service;

    @Autowired
    private PoistettuService poistetutService;

    @Autowired
    private SisaltoViiteService sisaltoviiteService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<OpetussuunnitelmaBaseDto> getAll(@PathVariable final Long ktId) {
        return service.getOpetussuunnitelmat(ktId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public OpetussuunnitelmaBaseDto add(
            @PathVariable final Long ktId,
            @RequestBody OpetussuunnitelmaDto opsDto) {
        return service.addOpetussuunnitelma(ktId, opsDto);
    }

    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    @ResponseBody
    public OpetussuunnitelmaDto get(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOpetussuunnitelma(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}", method = RequestMethod.PUT)
    @ResponseBody
    public OpetussuunnitelmaDto update(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @RequestBody(required = false) OpetussuunnitelmaDto body) {
        return service.update(ktId, opsId, body);
    }

    @RequestMapping(value = "/{opsId}/poistetut", method = RequestMethod.GET)
    @ResponseBody
    public List<PoistettuDto> getPoistetut(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return poistetutService.poistetut(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/poistetut/{poistettuId}/palauta", method = RequestMethod.POST)
    @ResponseBody
    public SisaltoViiteDto getPoistetut(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long poistettuId) {
        return sisaltoviiteService.restoreSisaltoViite(ktId, opsId, poistettuId);
    }

    @RequestMapping(value = "/{opsId}/oikeudet", method = RequestMethod.GET)
    public List<KayttajaoikeusDto> getOikeudet(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return service.getOikeudet(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/oikeudet/{kayttajaId}", method = RequestMethod.POST)
    public KayttajaoikeusDto updateOikeus(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Long kayttajaId,
            @RequestBody(required = false) KayttajaoikeusDto body) {
        return service.updateOikeus(ktId, opsId, kayttajaId, body);
    }

    @RequestMapping(value = "/{opsId}/versiot/uusin", method = RequestMethod.GET)
    @InternalApi
    Revision getLatestRevision(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return service.getLatestRevision(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/versiot", method = RequestMethod.GET)
    @InternalApi
    List<Revision> getRevisions(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId) {
        return service.getRevisions(ktId, opsId);
    }

    @RequestMapping(value = "/{opsId}/versiot/{revId}", method = RequestMethod.GET)
    @InternalApi
    Object getRevisions(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Integer revId) {
        return service.getData(ktId, opsId, revId);
    }

    @RequestMapping(value = "/{opsId}/tila/{tila}", method = RequestMethod.POST)
    public OpetussuunnitelmaBaseDto updateTila(
            @PathVariable final Long ktId,
            @PathVariable final Long opsId,
            @PathVariable final Tila tila) {
        return service.updateTila(ktId, opsId, tila);
    }


//    @RequestMapping(value = "/tekstit/{parentId}/lapsi/{childId}", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<TekstiKappaleViiteDto.Matala> addTekstiKappaleLapsi(
//            @PathVariable final Long opsId,
//            @PathVariable final Long parentId,
//            @PathVariable final Long childId) {
//        TekstiKappaleViiteDto.Matala viite = new TekstiKappaleViiteDto.Matala();
//        viite.setTekstiKappaleRef(Reference.of(childId));
//        return new ResponseEntity<>(
//                service().addTekstiKappaleLapsi(opsId, parentId, viite), HttpStatus.CREATED);
//    }

//    @RequestMapping(value = "/tekstit", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteDto.Puu> getTekstit(
//            @PathVariable final Long opsId) {
//        TekstiKappaleViiteDto.Puu dto = service().getTekstit(opsId, TekstiKappaleViiteDto.Puu.class);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteKevytDto> getTekstiOtsikot(@PathVariable final Long opsId) {
//        TekstiKappaleViiteKevytDto dto = service().getTekstit(opsId, TekstiKappaleViiteKevytDto.class);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteDto.Matala> getTekstiKappaleViite(
//            @PathVariable final Long opsId,
//            @PathVariable final Long viiteId) {
//        TekstiKappaleViiteDto.Matala dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/tekstit/{viiteId}/kaikki", method = RequestMethod.GET)
//    public ResponseEntity<TekstiKappaleViiteDto.Puu> getTekstiKappaleViiteSyva(
//            @PathVariable final Long opsId,
//            @PathVariable final Long viiteId) {
//        TekstiKappaleViiteDto.Puu dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId, TekstiKappaleViiteDto.Puu.class);
//        if (dto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }

//    @RequestMapping(value = "/tekstit/{viiteId}/muokattavakopio", method = RequestMeth*od.POST)
//    public TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(
//            @PathVariable final Long opsId,
//            @PathVariable final Long viiteId) {
//        return tekstiKappaleViiteService.kloonaaTekstiKappale(opsId, viiteId);
//    }
}

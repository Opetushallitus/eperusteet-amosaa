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
package fi.vm.sade.eperusteet.amosaa.resource.ops;

import com.mangofactory.swagger.annotations.ApiIgnore;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KommenttiDto;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.teksti.KommenttiService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mikkom
 */
@RestController
@RequestMapping("/kommentit")
@ApiIgnore
public class KommenttiController {

    @Autowired
    private KommenttiService service;

    @Autowired
    KayttajanTietoService kayttajanTietoService;

    private KommenttiDto rikastaKommentti(KommenttiDto kommentti) {
        if (kommentti != null) {
            KayttajanTietoDto kayttaja = kayttajanTietoService.hae(kommentti.getMuokkaaja());
            if (kayttaja != null) {
                String kutsumanimi = kayttaja.getKutsumanimi();
                String etunimet = kayttaja.getEtunimet();
                String etunimi = kutsumanimi != null ? kutsumanimi : etunimet;
                kommentti.setNimi(etunimi + " " + kayttaja.getSukunimi());
            }
        }
        return kommentti;
    }

    private KommenttiDto rikastaKommentti(KommenttiDto kommentti, Map<String, Future<KayttajanTietoDto>> kayttajat) {
        if (kayttajat.containsKey(kommentti.getMuokkaaja())) {
            try {
                KayttajanTietoDto kayttaja = kayttajat.get(kommentti.getMuokkaaja()).get(5, TimeUnit.SECONDS);
                if (kayttaja != null) {
                    String kutsumanimi = kayttaja.getKutsumanimi();
                    String etunimet = kayttaja.getEtunimet();
                    String etunimi = kutsumanimi != null ? kutsumanimi : etunimet;
                    kommentti.setNimi(etunimi + " " + kayttaja.getSukunimi());
                }
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                //ei välitetä epäonnistumisesta
            }
        }
        return kommentti;
    }

    private List<KommenttiDto> rikastaKommentit(List<KommenttiDto> kommentit) {

        Map<String, Future<KayttajanTietoDto>> kayttajat = kommentit.stream()
            .map(KommenttiDto::getMuokkaaja)
            .distinct()
            .collect(Collectors.toMap(s -> s, s -> kayttajanTietoService.haeAsync(s)));

        return kommentit.stream()
            .map(k -> rikastaKommentti(k, kayttajat))
            .collect(Collectors.toList());
    }

}

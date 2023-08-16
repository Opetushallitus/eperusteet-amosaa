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
package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.ValidointiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author nkala
 */
@Getter
public class Validointi {

    private List<Virhe> virheet = new ArrayList<>();
    private List<Virhe> varoitukset = new ArrayList<>();
    private List<Virhe> huomiot = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    static public class Virhe {
        private String kuvaus;
        private Map<Kieli, String> nimi;
        private NavigationNodeDto navigationNode;
    }

    public Validointi virhe(String kuvaus, NavigationNodeDto navigationNode) {
        virheet.add(new Virhe(kuvaus, null, navigationNode));
        return this;
    }

    public Validointi virhe(String kuvaus, NavigationNodeDto navigationNode, Map<Kieli, String> nimi) {
        virheet.add(new Virhe(kuvaus, nimi, navigationNode));
        return this;
    }

    public Validointi varoitus(String kuvaus, NavigationNodeDto navigationNode) {
        varoitukset.add(new Virhe(kuvaus, null, navigationNode));
        return this;
    }

    public Validointi varoitus(String kuvaus, NavigationNodeDto navigationNode, Map<Kieli, String> nimi) {
        varoitukset.add(new Virhe(kuvaus, nimi, navigationNode));
        return this;
    }

    public void tuomitse() {
        if (!virheet.isEmpty()) {
            throw new ValidointiException(this);
        }
    }
}

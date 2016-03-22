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
package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Yhteiset;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author isaul
 */
@Getter
@Setter
public class DokumenttiBase {
    Document document;
    Element headElement;
    Element bodyElement;
    Yhteiset yhteiset;
    CharapterNumberGenerator generator;
    Kieli kieli;
    Dokumentti dokumentti;
}

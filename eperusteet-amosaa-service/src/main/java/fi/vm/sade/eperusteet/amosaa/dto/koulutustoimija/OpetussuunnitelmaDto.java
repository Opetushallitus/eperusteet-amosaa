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

package fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nkala
 */
@Getter
@Setter
public class OpetussuunnitelmaDto extends OpetussuunnitelmaBaseDto {
    private String kommentti;
    private Set<Kieli> julkaisukielet;
    private Set<LiiteDto> liitteet;
    private Date paatospaivamaara;
    private Date voimaantulo;
    private String hyvaksyja;
    private String paatosnumero;
    private String suoritustapa;
    private boolean esikatseltavissa;
    private Set<String> tutkintonimikkeet = new HashSet<>();
    private Set<String> osaamisalat = new HashSet<>();
    private String oppilaitosTyyppiKoodiUri;
    private KoodistoKoodiDto oppilaitosTyyppiKoodi;
}

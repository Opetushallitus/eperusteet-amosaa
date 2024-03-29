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
package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class RakenneModuuliDto extends AbstractRakenneOsaDto implements VersionedDto {
    private LokalisoituTekstiDto nimi;
    private RakenneModuuliRooli rooli;
    private MuodostumisSaantoDto muodostumisSaanto;
    private OsaamisalaDto osaamisala;
    private KoodiDto tutkintonimike;
    private List<AbstractRakenneOsaDto> osat = new ArrayList<>();
    private Integer versioId;

    @Override
    public Integer getVersioId() {
        return versioId;
    }

    @Override
    public void setVersionId(Integer id) {
        versioId = id;
    }

    @Override
    protected void foreach(final Visitor visitor, final int depth) {
        visitor.visit(this, depth);
        if (osat != null) {
            for (AbstractRakenneOsaDto dto : osat) {
                dto.foreach(visitor, depth + 1);
            }
        }
    }

}

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
package fi.vm.sade.eperusteet.amosaa.service.ops;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
@PreAuthorize("isAuthenticated()")
public interface TekstiKappaleViiteService {
    TekstiKappaleViiteDto.Matala getTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long viiteId);

    <T> T getTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long viiteId, Class<T> t);

    <T> List<T> getTekstiKappaleViitteet(@P("baseId") Long baseId, @P("id") Long id, Class<T> t);

    TekstiKappaleViiteDto.Matala addTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long viiteId,
                                                       TekstiKappaleViiteDto.Matala viiteDto);

    TekstiKappaleViiteDto updateTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long rootViiteId, TekstiKappaleViiteDto uusi);

    void removeTekstiKappaleViite(@P("baseId") Long baseId, @P("id") Long id, Long viiteId);

    TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(@P("baseId") Long baseId, @P("id") Long id, Long viiteId);

    void reorderSubTree(@P("baseId") Long baseId, @P("id") Long id, Long rootViiteId, TekstiKappaleViiteDto.Puu uusi);
}

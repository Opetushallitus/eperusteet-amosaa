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
package fi.vm.sade.eperusteet.amosaa.service.locking;

import fi.vm.sade.eperusteet.amosaa.domain.Lukko;
import fi.vm.sade.eperusteet.amosaa.service.exception.LockingException;

/**
 * @author jhyoty
 */
public interface LockManager {

    Lukko lock(Long id);

    boolean isLockedByAuthenticatedUser(Long id);

    /**
     * Varmistaa että tunnistettu käyttäjä omistaa lukon,
     *
     * @param id lukon tunniste
     * @throws LockingException jos lukkoa ei ole tai sen omistaa toinen käyttäjä
     */
    void ensureLockedByAuthenticatedUser(Long id);

    Lukko getLock(Long id);

    boolean unlock(Long id);
}

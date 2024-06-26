package fi.vm.sade.eperusteet.amosaa.service.locking;

import fi.vm.sade.eperusteet.amosaa.domain.Lukko;
import fi.vm.sade.eperusteet.amosaa.service.exception.LockingException;

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

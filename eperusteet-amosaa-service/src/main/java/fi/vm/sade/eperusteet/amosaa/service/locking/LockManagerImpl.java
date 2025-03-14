package fi.vm.sade.eperusteet.amosaa.service.locking;

import fi.vm.sade.eperusteet.amosaa.domain.Lukko;
import fi.vm.sade.eperusteet.amosaa.dto.LukkoDto;
import fi.vm.sade.eperusteet.amosaa.service.exception.LockingException;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.time.Instant;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class LockManagerImpl implements LockManager {

    private final TransactionTemplate transaction;

    @Autowired
    private EntityManager em;

    @Autowired
    public LockManagerImpl(PlatformTransactionManager manager) {
        transaction = new TransactionTemplate(manager);
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Value("${fi.vm.sade.eperusteet.amosaa.lukitus.aikaSekunteina}")
    private int maxLockTime;

    @PreAuthorize("isAuthenticated()")
    @Override
    public Lukko lock(final Long id) {

        final String oid = SecurityUtil.getAuthenticatedPrincipal().getName();

        Lukko lukko;
        try {
            lukko = transaction.execute(status -> {
                final Lukko newLukko = new Lukko(id, oid, maxLockTime);
                Lukko current = getLock(id);
                if (current != null) {
                    em.refresh(current, LockModeType.PESSIMISTIC_WRITE);
                    if (oid.equals(current.getHaltijaOid())) {
                        current.refresh();
                    } else if (current.getVanhentuu().isBefore(Instant.now())) {
                        em.remove(current);
                        em.persist(newLukko);
                        current = newLukko;
                    }
                } else {
                    em.persist(newLukko);
                    current = newLukko;
                }
                return current;
            });
        } catch (TransactionException | DataAccessException | PersistenceException t) {

            // (todennäköisesti) samanaikaisesti toisessa transaktiossa lisätty sama lukko, yritetään lukea tämä.
            lukko = transaction.execute(status -> getLock(id));
        }

        if (lukko == null || !oid.equals(lukko.getHaltijaOid())) {
            throw new LockingException("Kohde on lukittu", LukkoDto.of(lukko));
        }

        return lukko;
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    @Transactional(readOnly = true)
    public boolean isLockedByAuthenticatedUser(Long id) {
        return isLockedByAuthenticatedUser(getLock(id));
    }

    /**
     * Varmistaa, että autentikoitunut käyttäjä omistaa lukon. Huom! lukitsee lukon transaktion ajaksi siten että sitä ei voi muuttaa/poistaa.
     *
     * @param id lukon tunniste
     */
    @PreAuthorize("isAuthenticated()")
    @Override
    @Transactional
    public void ensureLockedByAuthenticatedUser(Long id) {
        Lukko lukko = em.find(Lukko.class, id, LockModeType.PESSIMISTIC_READ);
        if (!isLockedByAuthenticatedUser(lukko)) {
            throw new LockingException("Käyttäjä ei omista lukkoa", LukkoDto.of(lukko));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    @Transactional(readOnly = false)
    public boolean unlock(Long id) {
        final Lukko lukko = em.find(Lukko.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (lukko == null) {
            return false;
        }
        if (isLockedByAuthenticatedUser(lukko)) {
            em.remove(lukko);
            return true;
        }
        throw new LockingException("Käyttäjä ei omista lukkoa", LukkoDto.of(lukko));
    }

    @Override
    @Transactional(readOnly = true)
    public Lukko getLock(Long id) {
        return em.find(Lukko.class, id);
    }

    private boolean isLockedByAuthenticatedUser(Lukko lukko) {
        final String oid = SecurityUtil.getAuthenticatedPrincipal().getName();
        return lukko != null && Objects.equals(lukko.getHaltijaOid(), oid);
    }

}

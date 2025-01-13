package fi.vm.sade.eperusteet.amosaa.service.security;

import jakarta.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Tietokantaoperaatiot oikeuksien hallintaan
 */
@Repository
public class PermissionRepository {

    @Autowired
    private EntityManager em;
}

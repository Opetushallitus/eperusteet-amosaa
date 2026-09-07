package fi.vm.sade.eperusteet.amosaa.domain;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;

import org.hibernate.envers.Audited;

import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

/**
 * Kantaluokka entiteeteille joista ylläpidetään luotu/muokattu -tietoja.
 */
@MappedSuperclass
public abstract class AbstractAuditedEntity implements Serializable {

    @Audited
    @Column(updatable = false)
    private Date luotu;

    @Audited
    @Getter
    @Column(updatable = false)
    private String luoja;

    @Audited
    @Column
    private Date muokattu;

    @Audited
    @Getter
    @Column
    private String muokkaaja;

    public Date getLuotu() {
        return luotu == null ? null : new Date(luotu.getTime());
    }

    public Date getMuokattu() {
        return muokattu == null ? null : new Date(muokattu.getTime());
    }

    public void muokattu() {
        this.muokattu = new Date();
    }

    @PrePersist
    private void prepersist() {
        muokattu = luotu = new Date();
        luoja = muokkaaja = currentUsername();
    }

    @PreUpdate
    private void preupdate() {
        muokattu = new Date();
        muokkaaja = currentUsername();
    }

    private static String currentUsername() {
        Principal principal = SecurityUtil.getAuthenticatedPrincipal();
        return principal != null ? principal.getName() : "tuntematon";
    }

}

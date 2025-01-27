package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import org.hibernate.envers.Audited;

/**
 * Kantaluokka entiteeteille joista ylläpidetään luotu/muokattu -tietoja.
 */
@MappedSuperclass
public abstract class AbstractAuditedEntity implements Serializable {

    @Audited
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Audited
    @Getter
    @Column(updatable = false)
    private String luoja;

    @Audited
    @Column
    @Temporal(TemporalType.TIMESTAMP)
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
        luoja = muokkaaja = SecurityUtil.getAuthenticatedPrincipal().getName();
    }

    @PreUpdate
    private void preupdate() {
        muokattu = new Date();
        muokkaaja = SecurityUtil.getAuthenticatedPrincipal().getName();
    }

}

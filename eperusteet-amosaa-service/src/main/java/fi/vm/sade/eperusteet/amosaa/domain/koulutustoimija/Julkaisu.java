package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@Setter
@Table(name = "julkaisu")
public class Julkaisu extends AbstractReferenceableEntity {

    @NotNull
    private int revision;

    @ManyToOne
    @JoinColumn(name = "opetussuunnitelma_id")
    @NotNull
    private Opetussuunnitelma opetussuunnitelma;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti tiedote;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Getter
    @NotNull
    private String luoja;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private JulkaisuData data;

    @ElementCollection
    @NotNull
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<Long> dokumentit = new HashSet<>();

    @PrePersist
    private void prepersist() {
        luotu = new Date();
        if (luoja == null) {
            luoja = SecurityUtil.getAuthenticatedPrincipal().getName();
        }
    }

}

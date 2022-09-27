package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name = "omaosaalue")
public class OmaOsaAlue extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    @Getter
    @Setter
    @Column(updatable = false)
    private String perusteenOsaAlueKoodi;

    @Getter
    @Setter
    @Column(updatable = false)
    private Long perusteenOsaAlueId;

    @Getter
    @Setter
    private boolean piilotettu = false;

    @Getter
    @Setter
    @Column(updatable = false, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OmaOsaAlueTyyppi tyyppi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti tavatjaymparisto;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti arvioinnista;

//    @Getter
//    @Setter
//    @OneToMany(orphanRemoval = true, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
//    @OrderColumn(name = "jnro")
//    private List<VapaaTeksti> vapaat = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "osaalueenosaamistavoitteet_omaosaalue",
            joinColumns = @JoinColumn(name = "omaosaalue_id"),
            inverseJoinColumns = @JoinColumn(name = "osaalueenosaamistavoitteet_id"))
    @OrderColumn(name = "jarjestys")
    @Getter
    @Setter
    private List<OsaAlueenOsaamistavoitteet> ammattitaitovaatimukset = new ArrayList<>();
}

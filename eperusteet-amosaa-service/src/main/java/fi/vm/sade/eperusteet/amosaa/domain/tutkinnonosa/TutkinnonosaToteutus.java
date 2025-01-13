package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Audited
@Table(name = "tutkinnonosa_toteutus")
public class TutkinnonosaToteutus extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Copyable<TutkinnonosaToteutus> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @ElementCollection
    private Set<String> koodit;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Tutkinnonosa tutkinnonosa;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    private LokalisoituTeksti otsikko;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Tekstiosa tavatjaymparisto;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Tekstiosa arvioinnista;

    @Getter
    @Setter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    @JoinTable(name = "tutkinnonosa_toteutus_vapaa_teksti",
            joinColumns = @JoinColumn(name = "tutkinnonosa_toteutus_id"),
            inverseJoinColumns = @JoinColumn(name = "vapaat_id"))
    private List<VapaaTeksti> vapaat = new ArrayList<>();

    @Getter
    @Setter
    private boolean oletustoteutus;

    @Override
    public TutkinnonosaToteutus copy(boolean deep) {
        TutkinnonosaToteutus result = new TutkinnonosaToteutus();

        result.setKoodit(new HashSet<>(this.getKoodit()));
        // tutkinnonosa asetetaan parentissa
        result.setOtsikko(this.getOtsikko());
        if (this.getTavatjaymparisto() != null) {
            result.setTavatjaymparisto(this.getTavatjaymparisto().copy());
        }

        if (this.getArvioinnista() != null) {
            result.setArvioinnista(this.getArvioinnista().copy());
        }

        List<VapaaTeksti> vapaat = this.getVapaat();
        if (!ObjectUtils.isEmpty(vapaat)) {
            for (VapaaTeksti vt : vapaat) {
                result.getVapaat().add(vt.copy());
            }
        }

        return result;
    }
}

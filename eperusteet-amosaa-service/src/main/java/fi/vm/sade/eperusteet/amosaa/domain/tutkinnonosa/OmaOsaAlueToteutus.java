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
import org.springframework.util.ObjectUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@Table(name = "omaosaaluetoteutus")
public class OmaOsaAlueToteutus extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti otsikko;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Tekstiosa tavatjaymparisto;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Tekstiosa arvioinnista;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    private List<VapaaTeksti> vapaat = new ArrayList<>();

    private boolean oletustoteutus;

    public OmaOsaAlueToteutus copy() {
        OmaOsaAlueToteutus result = new OmaOsaAlueToteutus();

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


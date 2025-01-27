package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.io.Serializable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Getter
@Setter
@Table(name = "opintokokonaisuus_tavoite")
@NoArgsConstructor
public class OpintokokonaisuusTavoite extends AbstractAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private Boolean perusteesta;

    @Column(name = "tavoite_koodi")
    private String tavoiteKoodi;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti tavoite;

    public OpintokokonaisuusTavoite(Boolean perusteesta, String tavoiteKoodi, LokalisoituTeksti tavoite) {
        this.perusteesta = perusteesta;
        this.tavoiteKoodi = tavoiteKoodi;
        this.tavoite = tavoite;
    }

    public OpintokokonaisuusTavoite(Boolean perusteesta, String tavoiteKoodi) {
        this.perusteesta = perusteesta;
        this.tavoiteKoodi = tavoiteKoodi;
    }

    public static OpintokokonaisuusTavoite copy(OpintokokonaisuusTavoite original) {
        if (original != null) {
            return new OpintokokonaisuusTavoite(original.getPerusteesta(), original.getTavoiteKoodi(), original.getTavoite());
        }

        return null;
    }
}

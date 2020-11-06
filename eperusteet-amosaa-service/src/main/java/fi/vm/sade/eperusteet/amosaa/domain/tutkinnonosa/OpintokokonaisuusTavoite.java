package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

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

    @NotNull
    @Column(name = "tavoite_koodi")
    private String tavoiteKoodi;

    public OpintokokonaisuusTavoite(Boolean perusteesta, String tavoiteKoodi) {
        this.perusteesta = perusteesta;
        this.tavoiteKoodi = tavoiteKoodi;
    }

    public static OpintokokonaisuusTavoite copy(OpintokokonaisuusTavoite original) {
        if (original != null) {
            return new OpintokokonaisuusTavoite(original.getPerusteesta(), original.getTavoiteKoodi());
        }

        return null;
    }
}

package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * Suorituspolku on erikoistettu versio muodostumissäännöstöstä.
 */
@Entity
@Audited
@Table(name = "suorituspolku")
public class Suorituspolku extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Copyable<Suorituspolku> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "suorituspolku")
    private Set<SuorituspolkuRivi> rivit = new HashSet<>();

    @Getter
    @Setter
    private Boolean naytaKuvausJulkisesti;

    @Getter
    @Setter
    private Boolean piilotaPerusteenTeksti;

    @Getter
    @Setter
    @Column(precision = 10, scale = 2, name = "osasuorituspolku_laajuus")
    private BigDecimal osasuorituspolkuLaajuus;

    @Override
    public Suorituspolku copy(boolean deep) {
        return copy(this);
    }

    public static Suorituspolku copy(Suorituspolku original) {
        if (original != null) {
            Suorituspolku result = new Suorituspolku();
            result.setNaytaKuvausJulkisesti(original.getNaytaKuvausJulkisesti());

            for (SuorituspolkuRivi rivi : original.getRivit()) {
                SuorituspolkuRivi copy = rivi.copy();
                copy.setSuorituspolku(result);
                result.getRivit().add(copy);
            }
            return result;
        }
        else {
            return null;
        }
    }
}

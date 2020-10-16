package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Getter
@Setter
@Table(name = "opintokokonaisuus_arviointi")
@NoArgsConstructor
public class OpintokokonaisuusArviointi extends AbstractAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private Boolean perusteesta;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti arviointi;

    public OpintokokonaisuusArviointi(Boolean perusteesta, LokalisoituTeksti arviointi) {
        this.perusteesta = perusteesta;
        this.arviointi = arviointi;
    }

}

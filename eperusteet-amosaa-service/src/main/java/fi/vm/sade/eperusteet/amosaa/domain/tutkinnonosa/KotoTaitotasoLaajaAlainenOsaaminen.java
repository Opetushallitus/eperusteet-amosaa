package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import java.io.Serializable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Getter
@Setter
@Table(name = "koto_taitotaso_laajaalainenosaaminen")
public class KotoTaitotasoLaajaAlainenOsaaminen extends AbstractAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String koodiUri;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti teksti;

    public static KotoTaitotasoLaajaAlainenOsaaminen copy(KotoTaitotasoLaajaAlainenOsaaminen original) {
        if (original != null) {
            KotoTaitotasoLaajaAlainenOsaaminen result = new KotoTaitotasoLaajaAlainenOsaaminen();
            result.setKoodiUri(original.getKoodiUri());

            if (original.getTeksti() != null) {
                result.setTeksti(LokalisoituTeksti.of(original.getTeksti().getTeksti()));
            }

            return result;
        } else {
            return null;
        }
    }
}

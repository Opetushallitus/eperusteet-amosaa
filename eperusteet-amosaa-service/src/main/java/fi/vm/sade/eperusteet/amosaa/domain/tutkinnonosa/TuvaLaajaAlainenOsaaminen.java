package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Audited
@Getter
@Setter
@Table(name = "tuva_laajaalainenosaaminen")
public class TuvaLaajaAlainenOsaaminen extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Kooditettu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nimiKoodi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti teksti;

    private boolean liite;

    public static TuvaLaajaAlainenOsaaminen copy(TuvaLaajaAlainenOsaaminen original) {
        if (original != null) {
            TuvaLaajaAlainenOsaaminen result = new TuvaLaajaAlainenOsaaminen();

            result.setNimiKoodi(original.getNimiKoodi());
            if (original.getTeksti() != null) {
                result.setTeksti(LokalisoituTeksti.of(original.getTeksti().getTeksti()));
            }

            result.setLiite(original.isLiite());

            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getUri() {
        return nimiKoodi;
    }
}

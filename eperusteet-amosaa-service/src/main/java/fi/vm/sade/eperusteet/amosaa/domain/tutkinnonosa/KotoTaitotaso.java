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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Getter
@Setter
@Table(name = "koto_taitotaso")
public class KotoTaitotaso extends AbstractAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String koodiUri;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti tavoiteTarkennus;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti sisaltoTarkennus;

    public static KotoTaitotaso of(String koodiUri) {
        KotoTaitotaso kotoTaitotaso = new KotoTaitotaso();
        kotoTaitotaso.setKoodiUri(koodiUri);
        return kotoTaitotaso;
    }

    public static KotoTaitotaso copy(KotoTaitotaso original) {
        if (original != null) {
            KotoTaitotaso result = new KotoTaitotaso();
            result.setKoodiUri(original.getKoodiUri());

            if (original.getTavoiteTarkennus() != null) {
                result.setTavoiteTarkennus(LokalisoituTeksti.of(original.getTavoiteTarkennus().getTeksti()));
            }

            if (original.getSisaltoTarkennus() != null) {
                result.setSisaltoTarkennus(LokalisoituTeksti.of(original.getSisaltoTarkennus().getTeksti()));
            }

            return result;
        } else {
            return null;
        }
    }
}

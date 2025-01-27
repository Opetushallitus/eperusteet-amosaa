package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
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
@Table(name = "koulutuksenosan_laajaalainen_osaaminen")
public class KoulutuksenosaLaajaalainenOsaaminen extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Kooditettu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String koodiUri;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti laajaAlaisenOsaamisenKuvaus;

    @ManyToOne
    private KoulutuksenosanPaikallinenTarkennus paikallinenTarkennus;

    public static KoulutuksenosaLaajaalainenOsaaminen copy(KoulutuksenosaLaajaalainenOsaaminen original) {
        if (original != null) {
            KoulutuksenosaLaajaalainenOsaaminen result = new KoulutuksenosaLaajaalainenOsaaminen();

            result.setKoodiUri(original.koodiUri);

            if (original.getLaajaAlaisenOsaamisenKuvaus() != null) {
                result.setLaajaAlaisenOsaamisenKuvaus(LokalisoituTeksti.of(original.getLaajaAlaisenOsaamisenKuvaus().getTeksti()));
            }

            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getUri() {
        return koodiUri;
    }
}

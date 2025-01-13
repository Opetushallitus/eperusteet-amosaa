package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@Table(name = "osaamismerkkikappale")
public class OsaamismerkkiKappale extends AbstractAuditedEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OsaamismerkkiKoodi> osaamismerkkiKoodit;

    public static OsaamismerkkiKappale copy(OsaamismerkkiKappale original) {
        if (original != null) {
            List<OsaamismerkkiKoodi> osaamismerkkiCopies = new ArrayList<>();
            for (OsaamismerkkiKoodi merkki : original.getOsaamismerkkiKoodit()) {
                osaamismerkkiCopies.add(OsaamismerkkiKoodi.copy(merkki));
            }

            OsaamismerkkiKappale result = new OsaamismerkkiKappale();
            result.setKuvaus(original.getKuvaus());
            result.setOsaamismerkkiKoodit(osaamismerkkiCopies);
            return result;
        }
        else {
            return null;
        }
    }
}

package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Audited
@Entity
@Getter
@Setter
@Table(name = "osaamismerkki_koodi")
public class OsaamismerkkiKoodi implements Serializable, ReferenceableEntity, Kooditettu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String koodi;

    @Override
    public String getUri() {
        return "osaamismerkit_" + koodi;
    }

    public static OsaamismerkkiKoodi copy(OsaamismerkkiKoodi original) {
        if (original != null) {
            OsaamismerkkiKoodi result = new OsaamismerkkiKoodi();
            result.setKoodi(original.getKoodi());
            return result;
        }
        else {
            return null;
        }
    }
}

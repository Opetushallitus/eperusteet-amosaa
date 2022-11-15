package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

import java.util.Objects;

import static fi.vm.sade.eperusteet.amosaa.service.util.Util.refXnor;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Table(name = "ammattitaitovaatimus2019")
@Audited
@NoArgsConstructor
public class Ammattitaitovaatimus2019 extends AbstractAuditedReferenceableEntity {
    @Setter
    @Getter
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private KoodistoKoodi koodi;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @Setter
    @Getter
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = NOT_AUDITED)
    private LokalisoituTeksti vaatimus;

    public static Ammattitaitovaatimus2019 of(LokalisoituTeksti tp) {
        Ammattitaitovaatimus2019 result = new Ammattitaitovaatimus2019();
        result.setVaatimus(tp);
        return result;
    }

    public boolean structureEquals(Ammattitaitovaatimus2019 other) {
        if (this == other) {
            return true;
        }
        boolean result = Objects.equals(getKoodi(), other.getKoodi());

        result &= refXnor(getVaatimus(), other.getVaatimus());

        return result;
    }

}

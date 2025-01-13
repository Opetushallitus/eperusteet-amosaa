package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static fi.vm.sade.eperusteet.amosaa.service.util.Util.refXnor;

@Entity
@Table(name = "ammattitaitovaatimus2019_kohdealue")
@Audited
@NoArgsConstructor
public class Ammattitaitovaatimus2019Kohdealue extends AbstractAuditedReferenceableEntity {

    @ValidHtml
    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @Getter
    @Setter
    @OrderColumn(name = "jarjestys")
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "ammattitaitovaatimus2019kohdealue_ammattitaitovaatimus",
            joinColumns = @JoinColumn(name = "kohdealue_id"),
            inverseJoinColumns = @JoinColumn(name = "ammattitaitovaatimus_id"))
    private List<Ammattitaitovaatimus2019> vaatimukset = new ArrayList<>();

    public Ammattitaitovaatimus2019Kohdealue(Ammattitaitovaatimus2019Kohdealue other) {
        this.kuvaus = other.kuvaus;

        if (other.getVaatimukset() != null) {
            this.vaatimukset = new ArrayList<>();
            for (Ammattitaitovaatimus2019 vaatimus : other.getVaatimukset()) {
                this.vaatimukset.add(Ammattitaitovaatimus2019.of(vaatimus.getVaatimus()));
            }
        }
    }

    public boolean structureEquals(Ammattitaitovaatimus2019Kohdealue other) {
        if (this == other) {
            return true;
        }
        boolean result = refXnor(getKuvaus(), other.getKuvaus());

        if (result && getVaatimukset() != null) {
            Iterator<Ammattitaitovaatimus2019> i = getVaatimukset().iterator();
            Iterator<Ammattitaitovaatimus2019> j = other.getVaatimukset().iterator();
            while (result && i.hasNext() && j.hasNext()) {
                result &= i.next().structureEquals(j.next());
            }
            result &= !i.hasNext();
            result &= !j.hasNext();
        }

        return result;
    }
}

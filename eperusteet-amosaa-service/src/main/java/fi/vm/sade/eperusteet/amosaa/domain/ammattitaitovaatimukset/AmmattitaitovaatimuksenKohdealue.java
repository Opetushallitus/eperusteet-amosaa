package fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by autio on 20.10.2015.
 */
@Entity
@Table(name = "ammattitaitovaatimuksenkohdealue")
@Audited
public class AmmattitaitovaatimuksenKohdealue implements Serializable {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti otsikko;

    @OneToMany(mappedBy = "ammattitaitovaatimuksenkohdealue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<AmmattitaitovaatimuksenKohde> vaatimuksenKohteet = new ArrayList<>();

    @Getter
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ammattitaitovaatimuksenkohdealue_tutkinnonosa",
            inverseJoinColumns = @JoinColumn(name = "tutkinnonosa_id"),
            joinColumns = @JoinColumn(name = "ammattitaitovaatimuksenkohdealue_id"))
    private Set<Tutkinnonosa> tutkinnonOsat = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AmmattitaitovaatimuksenKohdealue that = (AmmattitaitovaatimuksenKohdealue) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    public void connectAmmattitaitovaatimuksetToKohdealue(AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenKohdealue) {
        for (AmmattitaitovaatimuksenKohde ammattitaitovaatimuksenKohde : this.getVaatimuksenKohteet()) {
            ammattitaitovaatimuksenKohde.setAmmattitaitovaatimuksenkohdealue(ammattitaitovaatimuksenKohdealue);
            for (Ammattitaitovaatimus ammattitaitovaatimus : ammattitaitovaatimuksenKohde.getVaatimukset()) {
                ammattitaitovaatimus.setAmmattitaitovaatimuksenkohde(ammattitaitovaatimuksenKohde);
            }
        }
    }
}

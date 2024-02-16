package fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Table(name = "ammattitaitovaatimuksenkohdealue")
@Audited
public class AmmattitaitovaatimuksenKohdealue implements Serializable, Copyable<AmmattitaitovaatimuksenKohdealue> {

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
    @JoinTable(name = "ammattitaitovaatimuksenkohdealue_omatutkinnonosa",
            inverseJoinColumns = @JoinColumn(name = "omatutkinnonosa_id"),
            joinColumns = @JoinColumn(name = "ammattitaitovaatimuksenkohdealue_id"))
    private Set<OmaTutkinnonosa> tutkinnonOsat = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AmmattitaitovaatimuksenKohdealue that = (AmmattitaitovaatimuksenKohdealue) o;

        return Objects.equals(id, that.id);

    }

    public void connectAmmattitaitovaatimuksetToKohdealue(
            AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenKohdealue) {
        for (AmmattitaitovaatimuksenKohde ammattitaitovaatimuksenKohde : this.getVaatimuksenKohteet()) {
            ammattitaitovaatimuksenKohde.setAmmattitaitovaatimuksenkohdealue(ammattitaitovaatimuksenKohdealue);
            for (Ammattitaitovaatimus ammattitaitovaatimus : ammattitaitovaatimuksenKohde.getVaatimukset()) {
                ammattitaitovaatimus.setAmmattitaitovaatimuksenkohde(ammattitaitovaatimuksenKohde);
            }
        }
    }

    @Override
    public AmmattitaitovaatimuksenKohdealue copy(boolean deep) {
        AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenKohdealue = new AmmattitaitovaatimuksenKohdealue();

        // Todo: attribuutit
        ammattitaitovaatimuksenKohdealue.setOtsikko(this.getOtsikko());

        List<AmmattitaitovaatimuksenKohde> vaatimuksenKohteet = this.getVaatimuksenKohteet();
        if (!ObjectUtils.isEmpty(vaatimuksenKohteet)) {
            ammattitaitovaatimuksenKohdealue.setVaatimuksenKohteet(new ArrayList<>());

            for (AmmattitaitovaatimuksenKohde kohde : vaatimuksenKohteet) {
                AmmattitaitovaatimuksenKohde copy = kohde.copy();
                copy.setAmmattitaitovaatimuksenkohdealue(ammattitaitovaatimuksenKohdealue);
                ammattitaitovaatimuksenKohdealue.getVaatimuksenKohteet().add(copy);
            }
        }

        return ammattitaitovaatimuksenKohdealue;
    }
}

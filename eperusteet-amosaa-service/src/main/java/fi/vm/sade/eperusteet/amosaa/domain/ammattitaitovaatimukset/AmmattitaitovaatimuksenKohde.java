package fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Table(name = "ammattitaitovaatimuksenkohde")
@Audited
public class AmmattitaitovaatimuksenKohde implements Serializable, Copyable<AmmattitaitovaatimuksenKohde> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti otsikko;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti selite;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    private AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenkohdealue;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    @OrderColumn(name = "jarjestys")
    private List<Ammattitaitovaatimus> vaatimukset = new ArrayList<>();

    @Override
    public AmmattitaitovaatimuksenKohde copy(boolean deep) {
        AmmattitaitovaatimuksenKohde kohde = new AmmattitaitovaatimuksenKohde();

        kohde.setOtsikko(this.getOtsikko());
        kohde.setSelite(this.getSelite());

        if (!ObjectUtils.isEmpty(this.getVaatimukset())) {
            kohde.setVaatimukset(this.getVaatimukset().stream()
                    .map(Ammattitaitovaatimus::copy)
                    .collect(Collectors.toList()));
            kohde.getVaatimukset().forEach(kj -> kj.setAmmattitaitovaatimuksenkohde(kohde));
        }

        return kohde;
    }
}

package fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ammattitaitovaatimus")
@Audited
public class Ammattitaitovaatimus implements Serializable, Copyable<Ammattitaitovaatimus> {

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
    private LokalisoituTeksti selite;

    @Getter
    @Setter
    @Column(name = "koodi")
    private String ammattitaitovaatimusKoodi;

    @Getter
    @Setter
    private Integer jarjestys;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private AmmattitaitovaatimuksenKohde ammattitaitovaatimuksenkohde;

    @Override
    public Ammattitaitovaatimus copy(boolean deep) {
        Ammattitaitovaatimus ammattitaitovaatimus = new Ammattitaitovaatimus();

        ammattitaitovaatimus.setSelite(this.getSelite());
        ammattitaitovaatimus.setAmmattitaitovaatimusKoodi(this.getAmmattitaitovaatimusKoodi());
        ammattitaitovaatimus.setJarjestys(this.getJarjestys());

        return ammattitaitovaatimus;
    }
}

package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@Table(name = "omaosaalue")
public class OmaOsaAlue extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Kooditettu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    @Column(updatable = false)
    private String perusteenOsaAlueKoodi;

    @Column(updatable = false)
    private Long perusteenOsaAlueId;

    private boolean piilotettu = false;

    @Column(updatable = false, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OmaOsaAlueTyyppi tyyppi;

    private Integer laajuus;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Ammattitaitovaatimukset2019 osaamistavoitteet;

    private Long geneerinenarviointi;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    private List<OmaOsaAlueToteutus> toteutukset = new ArrayList<>();

    @Override
    public String getUri() {
        return perusteenOsaAlueKoodi;
    }
}

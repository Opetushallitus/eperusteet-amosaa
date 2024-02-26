package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti paikallinenTarkennus;

    @Getter
    @Setter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    private List<VapaaTeksti> vapaat = new ArrayList<>();

    private String koodi;

    @Override
    public String getUri() {
        return perusteenOsaAlueKoodi;
    }

    public OmaOsaAlue copy() {
        OmaOsaAlue result = new OmaOsaAlue();
        result.setNimi(getNimi());
        result.setPiilotettu(isPiilotettu());
        result.setTyyppi(getTyyppi());
        result.setLaajuus(getLaajuus());
        result.setKoodi(getKoodi());
        result.setPerusteenOsaAlueId(getPerusteenOsaAlueId());
        result.setPerusteenOsaAlueKoodi(getPerusteenOsaAlueKoodi());
        result.setPaikallinenTarkennus(getPaikallinenTarkennus());

        if (osaamistavoitteet != null) {
            result.setOsaamistavoitteet(new Ammattitaitovaatimukset2019(osaamistavoitteet));
        }
        result.setGeneerinenarviointi(getGeneerinenarviointi());

        if (!CollectionUtils.isEmpty(getToteutukset())) {
            result.toteutukset.addAll(getToteutukset().stream().map(OmaOsaAlueToteutus::copy).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(getVapaat())) {
            result.vapaat.addAll(getVapaat().stream().map(VapaaTeksti::copy).collect(Collectors.toList()));
        }
        return result;
    }

    public void asetaPaikallisetMaaritykset(OmaOsaAlue other) {
        this.setLaajuus(other.getLaajuus());

        List<OmaOsaAlueToteutus> toteutukset = other.getToteutukset();
        if (!ObjectUtils.isEmpty(toteutukset)) {
            this.getToteutukset().clear();
            for (OmaOsaAlueToteutus toteutus : toteutukset) {
                OmaOsaAlueToteutus copy = toteutus.copy();
                this.getToteutukset().add(copy);
            }
        }

        if (other.getOsaamistavoitteet() != null) {
            this.setOsaamistavoitteet(new Ammattitaitovaatimukset2019(other.getOsaamistavoitteet()));
        }
        this.setGeneerinenarviointi(other.geneerinenarviointi);

    }

    public int sort() {
        switch (tyyppi) {
            case PAKOLLINEN:
                return 0;
            case VALINNAINEN:
                return 1;
            case PAIKALLINEN:
                return 2;
            default:
                return 3;
        }
    }
}

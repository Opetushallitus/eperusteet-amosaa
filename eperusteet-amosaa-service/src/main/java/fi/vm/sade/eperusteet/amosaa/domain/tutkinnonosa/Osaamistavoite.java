package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import com.google.common.base.Objects;
import fi.vm.sade.eperusteet.amosaa.domain.PartialMergeable;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.AmmattitaitovaatimuksenKohdealue;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidOsaamistavoiteEsitieto;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.eperusteet.amosaa.service.util.Util.refXnor;

@Entity
@Table(name = "osaamistavoite")
@Audited
@ValidOsaamistavoiteEsitieto
public class Osaamistavoite implements Serializable, PartialMergeable<Osaamistavoite>, ReferenceableEntity, Copyable<Osaamistavoite> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    @Getter
    @Setter
    private boolean pakollinen;

    @Getter
    @Setter
    @Column(precision = 10, scale = 2)
    private BigDecimal laajuus;

    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti tavoitteet;

    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti tunnustaminen;

    @Getter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Arviointi arviointi;


    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "ammattitaitovaatimuksenkohdealue_osaamistavoite",
            joinColumns = @JoinColumn(name = "osaamistavoite_id"),
            inverseJoinColumns = @JoinColumn(name = "ammattitaitovaatimuksenkohdealue_id"))
    @Getter
    @Setter
    @OrderColumn(name = "jarjestys")
    private List<AmmattitaitovaatimuksenKohdealue> ammattitaitovaatimuksetLista = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Getter
    private Osaamistavoite esitieto;

    @Getter
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tutkinnonosa_osaalue_osaamistavoite",
            inverseJoinColumns = @JoinColumn(name = "tutkinnonosa_osaalue_id"),
            joinColumns = @JoinColumn(name = "osaamistavoite_id"))
    private Set<OsaAlue> osaAlueet;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    /**
     * Jos osaamistavoiteesta on vain yksi kieliversio, määritellään se tässä.
     */
    private Kieli kieli;

    @Column(name = "koodi_uri")
    @Getter
    @Setter
    private String koodiUri;

    @Column(name = "koodi_arvo")
    @Getter
    @Setter
    private String koodiArvo;


    public Osaamistavoite() {
    }

    Osaamistavoite(Osaamistavoite ot, Map<Osaamistavoite, Osaamistavoite> esitiedot) {
        this.nimi = ot.getNimi();
        this.pakollinen = ot.isPakollinen();
        this.laajuus = ot.getLaajuus();
        this.tunnustaminen = ot.getTunnustaminen();
        this.kieli = ot.kieli;
        this.arviointi = ot.getArviointi() == null ? null : new Arviointi(ot.getArviointi());

        if (ot.getEsitieto() != null) {
            this.esitieto = esitiedot.get(ot.getEsitieto());
            if (this.esitieto == null) {
                this.esitieto = new Osaamistavoite(ot.getEsitieto(), esitiedot);
                esitiedot.put(ot.getEsitieto(), this.esitieto);
            }
        }
    }

    public void setArviointi(Arviointi arviointi) {
        if (this.arviointi == null || arviointi == null || this.arviointi == arviointi) {
            this.arviointi = arviointi;
        } else {
            this.arviointi.mergeState(arviointi);
        }
    }

    @Override
    public void mergeState(Osaamistavoite updated) {
        if (updated != null) {
            this.setNimi(updated.getNimi());
            this.setPakollinen(updated.isPakollinen());
            this.setKieli(updated.getKieli());
            this.setLaajuus(updated.getLaajuus());
            this.setTavoitteet(updated.getTavoitteet());
            this.setTunnustaminen(updated.getTunnustaminen());
            this.setArviointi(updated.getArviointi());
            connectAmmattitaitovaatimusListToTutkinnonOsa(updated);
            this.setAmmattitaitovaatimuksetLista(updated.getAmmattitaitovaatimuksetLista());
            this.setEsitieto(updated.getEsitieto());
        }
    }

    private List<AmmattitaitovaatimuksenKohdealue> connectAmmattitaitovaatimusListToTutkinnonOsa(Osaamistavoite other) {
        for (AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenKohdealue : other.getAmmattitaitovaatimuksetLista()) {
            ammattitaitovaatimuksenKohdealue.connectAmmattitaitovaatimuksetToKohdealue(ammattitaitovaatimuksenKohdealue);
        }
        return other.getAmmattitaitovaatimuksetLista();
    }

    @Override
    public void partialMergeState(Osaamistavoite updated) {
        if (updated != null) {
            this.setNimi(updated.getNimi());
            this.setPakollinen(updated.isPakollinen());
            this.setLaajuus(updated.getLaajuus());
            this.setKieli(updated.getKieli());
        }
    }

    public void setEsitieto(Osaamistavoite esitieto) {
        if (this == esitieto) {
            throw new IllegalArgumentException("Osaamistavoite ei voi olla oma esitietonsa");
        }
        this.esitieto = esitieto;
    }

    public boolean structureEquals(Osaamistavoite other) {
        boolean result = refXnor(getNimi(), other.getNimi());
        result &= isPakollinen() == other.isPakollinen();
        result &= Objects.equal(getLaajuus(), other.getLaajuus());
        result &= refXnor(getTavoitteet(), other.getTavoitteet());
        result &= refXnor(getTunnustaminen(), other.getTunnustaminen());
        result &= refXnor(getEsitieto(), other.getEsitieto());
        result &= refXnor(getArviointi(), other.getArviointi());
        if (getArviointi() != null) {
            result &= getArviointi().structureEquals(other.getArviointi());
        }
        return result;
    }

    @Override
    public Osaamistavoite copy(boolean deep) {
        Osaamistavoite osaamistavoite = new Osaamistavoite();

        osaamistavoite.setNimi(this.getNimi());
        osaamistavoite.setPakollinen(this.isPakollinen());
        osaamistavoite.setLaajuus(this.getLaajuus());
        osaamistavoite.setTavoitteet(this.getTavoitteet());
        osaamistavoite.setTunnustaminen(this.getTunnustaminen());
        Arviointi arviointi = this.getArviointi();
        if (arviointi != null) {
            osaamistavoite.setArviointi(arviointi.copy());
        }

        List<AmmattitaitovaatimuksenKohdealue> ammattitaitovaatimuksetLista = this.getAmmattitaitovaatimuksetLista();
        if (!ObjectUtils.isEmpty(ammattitaitovaatimuksetLista)) {
            osaamistavoite.setAmmattitaitovaatimuksetLista(new ArrayList<>());

            for (AmmattitaitovaatimuksenKohdealue kohdealue: ammattitaitovaatimuksetLista) {
                osaamistavoite.getAmmattitaitovaatimuksetLista().add(kohdealue.copy());
            }
        }

        Osaamistavoite esitieto = this.getEsitieto();
        if (esitieto != null) {
            osaamistavoite.setEsitieto(esitieto.copy());
        }

        return osaamistavoite;
    }
}

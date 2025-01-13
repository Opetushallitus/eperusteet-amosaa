package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.AmmattitaitovaatimuksenKohdealue;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Audited
@Table(name = "omatutkinnonosa")
public class OmaTutkinnonosa extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti tavoitteet;

    @Getter
    @Setter
    @Column(name = "koodi_prefix")
    private String koodiPrefix;

    @Getter
    @Setter
    @Column(precision = 10, scale = 2)
    private BigDecimal laajuus;

    @Getter
    @Setter
    private String koodi;

    @Getter
    @Setter
    private Long geneerinenarviointi;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Getter
    @Setter
    private Ammattitaitovaatimukset2019 ammattitaitovaatimukset;

    @Deprecated
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "ammattitaitovaatimuksenkohdealue_omatutkinnonosa",
            joinColumns = @JoinColumn(name = "omatutkinnonosa_id"),
            inverseJoinColumns = @JoinColumn(name = "ammattitaitovaatimuksenkohdealue_id"))
    @OrderColumn(name = "jarjestys")
    @Getter
    @Setter
    private List<AmmattitaitovaatimuksenKohdealue> ammattitaitovaatimuksetLista = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private Arviointi arviointi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti ammattitaidonOsoittamistavat;

    public void setArviointi(Arviointi arviointi) {
        if (Objects.equals(this.arviointi, arviointi)) {
            return;
        }
        if (arviointi == null || this.arviointi == null) {
            this.arviointi = arviointi;
        } else {
            this.arviointi.mergeState(arviointi);
            this.muokattu();
        }
    }

    static OmaTutkinnonosa copy(OmaTutkinnonosa original) {
        if (original != null) {
            OmaTutkinnonosa result = new OmaTutkinnonosa();

            result.setTavoitteet(original.getTavoitteet());
            result.setKoodiPrefix(original.getKoodiPrefix());
            result.setLaajuus(original.getLaajuus());
            result.setKoodi(original.getKoodi());

            List<AmmattitaitovaatimuksenKohdealue> ammattitaitovaatimuksetLista
                    = original.getAmmattitaitovaatimuksetLista();
            if (!ObjectUtils.isEmpty(ammattitaitovaatimuksetLista)) {
                result.setAmmattitaitovaatimuksetLista(new ArrayList<>());
                for (AmmattitaitovaatimuksenKohdealue ammattitaitovaatimus : ammattitaitovaatimuksetLista) {
                    result.getAmmattitaitovaatimuksetLista().add(ammattitaitovaatimus.copy());
                }
            }

            Arviointi arviointi = original.getArviointi();
            if (arviointi != null) {
                result.setArviointi(arviointi.copy());
            }

            result.setAmmattitaidonOsoittamistavat(original.getAmmattitaidonOsoittamistavat());

            if (original.getAmmattitaitovaatimukset() != null) {
                result.setAmmattitaitovaatimukset(new Ammattitaitovaatimukset2019(original.getAmmattitaitovaatimukset()));
            }
            result.setGeneerinenarviointi(original.geneerinenarviointi);

            return result;
        }
        else {
            return null;
        }
    }
}

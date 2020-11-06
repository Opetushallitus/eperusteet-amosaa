package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Audited
@Getter
@Setter
@Table(name = "opintokokonaisuus")
public class Opintokokonaisuus extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(updatable = false)
    private String nimiKoodi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    private Integer laajuus;

    private Integer minimilaajuus;

    @Enumerated(EnumType.STRING)
    private OpintokokonaisuusTyyppi tyyppi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti opetuksenTavoiteOtsikko;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn
    private List<OpintokokonaisuusTavoite> tavoitteet = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti tavoitteidenKuvaus;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti keskeisetSisallot;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti arvioinninKuvaus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn
    private List<OpintokokonaisuusArviointi> arvioinnit = new ArrayList<>();

    public static Opintokokonaisuus copy(Opintokokonaisuus original) {
        if (original != null) {
            Opintokokonaisuus result = new Opintokokonaisuus();

            result.setTyyppi(original.getTyyppi());
            result.setNimiKoodi(original.getNimiKoodi());
            result.setLaajuus(original.getLaajuus());
            result.setMinimilaajuus(original.getMinimilaajuus());
            if (original.getKuvaus() != null) {
                result.setKuvaus(LokalisoituTeksti.of(original.getKuvaus().getTeksti()));
            }
            if (original.getOpetuksenTavoiteOtsikko() != null) {
                result.setOpetuksenTavoiteOtsikko(LokalisoituTeksti.of(original.getOpetuksenTavoiteOtsikko().getTeksti()));
            }
            if (original.getTavoitteidenKuvaus() != null) {
                result.setTavoitteidenKuvaus(LokalisoituTeksti.of(original.getTavoitteidenKuvaus().getTeksti()));
            }
            if (original.getKeskeisetSisallot() != null) {
                result.setKeskeisetSisallot(LokalisoituTeksti.of(original.getKeskeisetSisallot().getTeksti()));
            }
            if (original.getArvioinninKuvaus() != null) {
                result.setArvioinninKuvaus(LokalisoituTeksti.of(original.getArvioinninKuvaus().getTeksti()));
            }

            if (!ObjectUtils.isEmpty(original.getTavoitteet())) {
                result.setTavoitteet(original.getTavoitteet().stream().map(OpintokokonaisuusTavoite::copy).collect(Collectors.toList()));
            }

            if (!ObjectUtils.isEmpty(original.getArvioinnit())) {
                result.setArvioinnit(original.getArvioinnit().stream().map(OpintokokonaisuusArviointi::copy).collect(Collectors.toList()));
            }

            return result;
        } else {
            return null;
        }
    }
}

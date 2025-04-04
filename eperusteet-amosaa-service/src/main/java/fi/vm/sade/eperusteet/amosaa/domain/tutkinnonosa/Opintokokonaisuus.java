package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import fi.vm.sade.eperusteet.amosaa.dto.peruste.LaajuusYksikko;
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
public class Opintokokonaisuus extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Kooditettu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(updatable = false)
    private String nimiKoodi;

    private String koodi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @Column(precision = 10, scale = 2)
    private BigDecimal laajuus;

    @Enumerated(EnumType.STRING)
    private LaajuusYksikko laajuusYksikko;

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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private OsaamismerkkiKappale osaamismerkkiKappale;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn
    private List<OpintokokonaisuusArviointi> arvioinnit = new ArrayList<>();

    public static Opintokokonaisuus copy(Opintokokonaisuus original) {
        if (original != null) {
            Opintokokonaisuus result = new Opintokokonaisuus();

            result.setTyyppi(original.getTyyppi());
            result.setNimiKoodi(original.getNimiKoodi());
            result.setLaajuus(original.getLaajuus());
            result.setLaajuusYksikko(original.getLaajuusYksikko());
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

            if (original.getOsaamismerkkiKappale() != null) {
                result.setOsaamismerkkiKappale(OsaamismerkkiKappale.copy(original.getOsaamismerkkiKappale()));
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

    @Override
    public String getUri() {
        return koodi;
    }
}

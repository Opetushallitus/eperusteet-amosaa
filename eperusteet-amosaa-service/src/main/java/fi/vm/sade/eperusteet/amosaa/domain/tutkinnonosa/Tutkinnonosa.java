package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.VapaaTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Audited
@Table(name = "tutkinnonosa")
public class Tutkinnonosa extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private TutkinnonosaTyyppi tyyppi;

    @Getter
    @Setter
    @Column(updatable = false)
    private String koodi;

    @Getter
    @Setter
    @Column(updatable = false)
    private Long perusteentutkinnonosa; // FIXME Käytä mahdollisesti tunnistetta

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti osaamisenOsoittaminen;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private OmaTutkinnonosa omatutkinnonosa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter
    @Setter
    private VierasTutkinnonosa vierastutkinnonosa;

    @Getter
    @Setter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    private List<TutkinnonosaToteutus> toteutukset = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "jnro")
    private List<VapaaTeksti> vapaat = new ArrayList<>();

    public static Tutkinnonosa copy(Tutkinnonosa original) {
        if (original != null) {
            Tutkinnonosa result = new Tutkinnonosa();

            result.setTyyppi(original.getTyyppi());
            result.setKoodi(original.getKoodi());
            result.setPerusteentutkinnonosa(original.getPerusteentutkinnonosa());
            result.setOsaamisenOsoittaminen(original.getOsaamisenOsoittaminen());
            result.setOmatutkinnonosa(OmaTutkinnonosa.copy(original.getOmatutkinnonosa()));
            result.setVierastutkinnonosa(VierasTutkinnonosa.copy(original.getVierastutkinnonosa()));

            List<TutkinnonosaToteutus> toteutukset = original.getToteutukset();
            if (!ObjectUtils.isEmpty(toteutukset)) {
                result.setToteutukset(new ArrayList<>());
                for (TutkinnonosaToteutus toteutus : toteutukset) {
                    TutkinnonosaToteutus copy = toteutus.copy();
                    copy.setTutkinnonosa(result);
                    result.getToteutukset().add(copy);
                }
            }

            List<VapaaTeksti> vapaat = original.getVapaat();
            if (!ObjectUtils.isEmpty(vapaat)) {
                result.setVapaat(new ArrayList<>());
                for (VapaaTeksti vapaa : vapaat) {
                    result.getVapaat().add(vapaa.copy());
                }
            }

            return result;
        } else {
            return null;
        }
    }

    public void asetaPaikallisetMaaritykset(Tutkinnonosa other) {
        this.setOsaamisenOsoittaminen(other.getOsaamisenOsoittaminen());
        this.setOmatutkinnonosa(OmaTutkinnonosa.copy(other.getOmatutkinnonosa()));
        this.setVierastutkinnonosa(VierasTutkinnonosa.copy(other.getVierastutkinnonosa()));

        List<TutkinnonosaToteutus> toteutukset = other.getToteutukset();
        if (!ObjectUtils.isEmpty(toteutukset)) {
            this.getToteutukset().clear();
            for (TutkinnonosaToteutus toteutus : toteutukset) {
                TutkinnonosaToteutus copy = toteutus.copy();
                copy.setTutkinnonosa(this);
                this.getToteutukset().add(copy);
            }
        }

        List<VapaaTeksti> vapaat = other.getVapaat();
        if (!ObjectUtils.isEmpty(vapaat)) {
            this.getVapaat().clear();
            for (VapaaTeksti vapaa : vapaat) {
                this.getVapaat().add(vapaa.copy());
            }
        }
    }
}

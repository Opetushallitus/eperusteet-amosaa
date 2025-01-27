package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;

import java.io.Serializable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Audited
@Table(name = "tekstiosa")
@Entity
public class Tekstiosa implements Serializable, Copyable<Tekstiosa> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    private LokalisoituTeksti otsikko;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml
    private LokalisoituTeksti teksti;

    public Tekstiosa() {
    }

    public Tekstiosa(LokalisoituTeksti otsikko, LokalisoituTeksti teksti) {
        this.otsikko = otsikko;
        this.teksti = teksti;
    }

    public Tekstiosa(Tekstiosa other) {
        this.otsikko = other.getOtsikko();
        this.teksti = other.getTeksti();
    }

    public static Tekstiosa copyOf(Tekstiosa other) {
        if (other == null) return null;
        return new Tekstiosa(other);
    }

    @Override
    public Tekstiosa copy(boolean deep) {
        return copyOf(this);
    }
}

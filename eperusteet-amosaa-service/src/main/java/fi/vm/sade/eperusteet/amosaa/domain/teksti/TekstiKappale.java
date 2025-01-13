package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tekstikappale")
@Audited
public class TekstiKappale extends AbstractAuditedEntity
        implements Serializable, ReferenceableEntity, Copyable<TekstiKappale>, HistoriaTapahtuma {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Column(updatable = false)
    private UUID tunniste;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti nimi;

    @Getter
    @Setter
    private Boolean valmis;

    /**
     * Kertoo että onko viitattava tekstikappale merkitty pakolliseksi
     * ts. sitä ei voi poistaa eikä sen otsikkoa muokata.
     */
    @Getter
    @Setter
    private Boolean pakollinen;

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti teksti;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    private Tila tila = Tila.LUONNOS;

    public TekstiKappale() {
        tunniste = UUID.randomUUID();
    }

    public TekstiKappale(TekstiKappale other) {
        this.tunniste = other.tunniste;
        copyState(other);
    }

    public void setTila(Tila tila) {
        if (this.tila == null || this.tila == Tila.LUONNOS) {
            this.tila = tila;
        }
    }

    public static TekstiKappale copy(TekstiKappale tk) {
        if (tk != null) {
            return new TekstiKappale(tk);
        }
        return null;
    }

    public TekstiKappale copy() {
        return new TekstiKappale(this);
    }

    private void copyState(TekstiKappale other) {
        this.setNimi(other.getNimi());
        this.setTeksti(other.getTeksti());
    }

    @Override
    public TekstiKappale copy(boolean deep) {
        TekstiKappale result = new TekstiKappale();
        result.setNimi(this.getNimi());
        result.setTeksti(this.getTeksti());
        result.setTila(this.getTila());
        result.tunniste = this.tunniste;
        result.setPakollinen(this.getPakollinen());
        return result;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.tekstikappale;
    }
}

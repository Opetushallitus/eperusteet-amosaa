package fi.vm.sade.eperusteet.amosaa.domain.arviointi;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Osaamistavoite;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static fi.vm.sade.eperusteet.amosaa.service.util.Util.refXnor;

@Entity
@Table(name = "arviointi")
@Audited
public class Arviointi implements Serializable, Copyable<Arviointi> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti lisatiedot;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinTable(name = "arviointi_arvioinninkohdealue",
            joinColumns = @JoinColumn(name = "arviointi_id"),
            inverseJoinColumns = @JoinColumn(name = "arvioinninkohdealue_id"))
    @OrderColumn
    @Getter
    private List<ArvioinninKohdealue> arvioinninKohdealueet = new ArrayList<>();

    @Getter
    @Setter
    @NotAudited
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "arviointi")
    private Osaamistavoite osaamistavoite;

    public Arviointi() {
    }

    public Arviointi(Arviointi other) {
        copyState(other);
    }

    public void setArvioinninKohdealueet(List<ArvioinninKohdealue> alueet) {
        if (this.arvioinninKohdealueet == alueet) {
            return;
        }
        if (alueet != null) {
            ArrayList<ArvioinninKohdealue> tmp = new ArrayList<>(alueet.size());
            for (ArvioinninKohdealue a : alueet) {
                int i = this.arvioinninKohdealueet.indexOf(a);
                if (i >= 0) {
                    tmp.add(this.arvioinninKohdealueet.get(i));
                } else {
                    tmp.add(a);
                }
            }
            this.arvioinninKohdealueet.clear();
            this.arvioinninKohdealueet.addAll(tmp);
        } else {
            this.arvioinninKohdealueet.clear();
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.lisatiedot);
        hash = 67 * hash + Objects.hashCode(this.arvioinninKohdealueet);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Arviointi) {
            final Arviointi other = (Arviointi) obj;
            if (!Objects.equals(this.lisatiedot, other.lisatiedot)) {
                return false;
            }
            return Objects.equals(this.arvioinninKohdealueet, other.arvioinninKohdealueet);
        }
        return false;
    }

    public void mergeState(Arviointi other) {
        if (this != other) {
            this.setLisatiedot(other.getLisatiedot());
            this.setArvioinninKohdealueet(other.getArvioinninKohdealueet());
        }
    }

    public boolean structureEquals(Arviointi other) {
        if (this == other) {
            return true;
        }
        boolean result = refXnor(getLisatiedot(), other.getLisatiedot());
        Iterator<ArvioinninKohdealue> i = getArvioinninKohdealueet().iterator();
        Iterator<ArvioinninKohdealue> j = other.getArvioinninKohdealueet().iterator();
        while (result && i.hasNext() && j.hasNext()) {
            result &= i.next().structureEquals(j.next());
        }
        result &= !i.hasNext();
        result &= !j.hasNext();
        return result;
    }

    private void copyState(Arviointi other) {
        this.setLisatiedot(other.getLisatiedot());
        for (ArvioinninKohdealue aka : other.getArvioinninKohdealueet()) {
            this.arvioinninKohdealueet.add(new ArvioinninKohdealue(aka));
        }
    }

    @Override
    public Arviointi copy(boolean deep) {
        Arviointi arviointi = new Arviointi();

        arviointi.setLisatiedot(this.getLisatiedot());

        List<ArvioinninKohdealue> arvioinninKohdealueet = this.getArvioinninKohdealueet();
        if (!ObjectUtils.isEmpty(arvioinninKohdealueet)) {
            arviointi.setArvioinninKohdealueet(new ArrayList<>());

            for (ArvioinninKohdealue kohdealue : arvioinninKohdealueet) {
                arviointi.getArvioinninKohdealueet().add(kohdealue.copy());
            }
        }

        Osaamistavoite osaamistavoite = this.getOsaamistavoite();
        if (osaamistavoite != null) {
            arviointi.setOsaamistavoite(osaamistavoite.copy());
        }

        return arviointi;
    }
}

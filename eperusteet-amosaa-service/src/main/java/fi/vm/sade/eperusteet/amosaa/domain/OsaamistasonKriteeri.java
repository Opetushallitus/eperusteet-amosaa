package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.arviointi.ArvioinninKohde;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "osaamistasonkriteeri")
@Audited
public class OsaamistasonKriteeri implements Serializable, Copyable<OsaamistasonKriteeri> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Osaamistaso osaamistaso;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @OrderColumn
    @JoinTable(name = "osaamistasonkriteeri_tekstipalanen",
            joinColumns = @JoinColumn(name = "osaamistasonkriteeri_id"),
            inverseJoinColumns = @JoinColumn(name = "tekstipalanen_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @BatchSize(size = 25)
    private List<LokalisoituTeksti> kriteerit = new ArrayList<>();

    @Getter
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "arvioinninkohde_osaamistasonkriteeri",
            joinColumns = @JoinColumn(name = "osaamistasonKriteerit_id", updatable = false, nullable = false),
            inverseJoinColumns = @JoinColumn(name = "arvioinninkohde_id", nullable = false, updatable = false))
    private Set<ArvioinninKohde> arvioinninKohteet = new HashSet<>();

    public OsaamistasonKriteeri() {
    }

    public OsaamistasonKriteeri(OsaamistasonKriteeri other) {
        this.osaamistaso = other.osaamistaso;
        this.kriteerit.addAll(other.kriteerit);
    }

    public List<LokalisoituTeksti> getKriteerit() {
        return new ArrayList<>(kriteerit);
    }

    public void setKriteerit(List<LokalisoituTeksti> kriteerit) {
        this.kriteerit.clear();
        if (kriteerit != null) {
            for (LokalisoituTeksti k : kriteerit) {
                if (k != null) {
                    this.kriteerit.add(k);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.osaamistaso);
        hash = 29 * hash + Objects.hashCode(this.kriteerit);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OsaamistasonKriteeri) {
            final OsaamistasonKriteeri other = (OsaamistasonKriteeri) obj;
            if (!Objects.equals(this.osaamistaso, other.osaamistaso)) {
                return false;
            }
            return Objects.equals(this.kriteerit, other.kriteerit);
        }
        return false;
    }

    @Override
    public OsaamistasonKriteeri copy(boolean deep) {
        OsaamistasonKriteeri kriteeri = new OsaamistasonKriteeri();

        kriteeri.setOsaamistaso(this.getOsaamistaso());
        kriteeri.setKriteerit(this.getKriteerit());

        return kriteeri;
    }
}

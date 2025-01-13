package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.PartialMergeable;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.*;

import static fi.vm.sade.eperusteet.amosaa.service.util.Util.refXnor;

@Entity
@Table(name = "tutkinnonosa_osaalue")
@Audited
public class OsaAlue implements Serializable, PartialMergeable<OsaAlue> {

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
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @Getter
    //@Setter
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "tutkinnonosa_osaalue_osaamistavoite",
            joinColumns = @JoinColumn(name = "tutkinnonosa_osaalue_id"),
            inverseJoinColumns = @JoinColumn(name = "osaamistavoite_id"))
    @OrderColumn
    private List<Osaamistavoite> osaamistavoitteet;

    @Getter
    @Setter
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tutkinnonosa_tutkinnonosa_osaalue",
            inverseJoinColumns = @JoinColumn(name = "tutkinnonosa_id"),
            joinColumns = @JoinColumn(name = "tutkinnonosa_osaalue_id"))
    private Set<Tutkinnonosa> tutkinnonOsat;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    /**
     * Jos osa-alueesta on vain yksi kieliversio, määritellään se tässä.
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

    public OsaAlue() {
    }

    public OsaAlue(OsaAlue o) {
        this.nimi = o.nimi;
        this.kuvaus = o.kuvaus;
        this.osaamistavoitteet = new ArrayList<>();
        IdentityHashMap<Osaamistavoite, Osaamistavoite> identityMap = new IdentityHashMap<>();
        for (Osaamistavoite ot : o.getOsaamistavoitteet()) {
            if (identityMap.containsKey(ot)) {
                this.osaamistavoitteet.add(identityMap.get(ot));
            } else {
                Osaamistavoite t = new Osaamistavoite(ot, identityMap);
                identityMap.put(ot, t);
                this.osaamistavoitteet.add(t);
            }
        }
    }

    public void setOsaamistavoitteet(List<Osaamistavoite> osaamistavoitteet) {
        if (this.osaamistavoitteet == null) {
            this.osaamistavoitteet = new ArrayList<>();
        }
        this.osaamistavoitteet.clear();
        if (osaamistavoitteet != null) {
            this.osaamistavoitteet.addAll(osaamistavoitteet);
        }
    }

    public boolean structureEquals(OsaAlue other) {
        boolean result = refXnor(getNimi(), other.getNimi());
        result &= refXnor(getKuvaus(), other.getKuvaus());
        if (result && getOsaamistavoitteet() != null && other.getOsaamistavoitteet() != null) {
            Iterator<Osaamistavoite> i = getOsaamistavoitteet().iterator();
            Iterator<Osaamistavoite> j = other.getOsaamistavoitteet().iterator();
            while (result && i.hasNext() && j.hasNext()) {
                result &= i.next().structureEquals(j.next());
            }
            result &= !i.hasNext();
            result &= !j.hasNext();
        }
        return result;
    }

    private List<Osaamistavoite> mergeOsaamistavoitteet(List<Osaamistavoite> current, List<Osaamistavoite> updated) {
        List<Osaamistavoite> tempList = new ArrayList<>();
        boolean loyty = false;
        if (updated != null) {
            for (Osaamistavoite osaamistavoiteUpdate : updated) {
                for (Osaamistavoite osaamistavoiteCurrent : current) {
                    if (osaamistavoiteCurrent.getId().equals(osaamistavoiteUpdate.getId())) {
                        // Jos osa-alueella osaamistavoitelista mergessä, niin kyseessä on kevyempi
                        // osaamistavoite objekteja. Joten käytetään partialMergeStatea.
                        //osaamistavoiteCurrent.partialMergeState(osaamistavoiteUpdate);
                        osaamistavoiteCurrent.mergeState(osaamistavoiteUpdate);
                        tempList.add(osaamistavoiteCurrent);
                        loyty = true;
                    }
                }
                if (!loyty) {
                    tempList.add(osaamistavoiteUpdate);
                }
                loyty = false;
            }
        }
        return tempList;
    }

    @Override
    public void partialMergeState(OsaAlue updated) {

    }

    @Override
    public void mergeState(OsaAlue updated) {

    }
}

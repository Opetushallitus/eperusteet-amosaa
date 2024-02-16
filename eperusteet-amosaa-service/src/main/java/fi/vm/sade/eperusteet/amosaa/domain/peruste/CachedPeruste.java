package fi.vm.sade.eperusteet.amosaa.domain.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.KoulutuksetConverter;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "peruste_cache")
public class CachedPeruste implements Serializable, ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti nimi;

    private String diaarinumero;

    @Column(columnDefinition = "text")
    private String peruste;

    @Column(name = "peruste_id")
    private Long perusteId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Enumerated(EnumType.STRING)
    private KoulutusTyyppi koulutustyyppi;

    //TODO add koulutustyyppitoteutus

    @Deprecated
    @Basic
    @Column(columnDefinition = "text")
    @Convert(converter = KoulutuksetConverter.class)
    private Set<KoulutusDto> koulutukset;

    @OneToMany(mappedBy = "cachedPeruste", cascade = {CascadeType.MERGE}, orphanRemoval = true)
    private Set<Koulutuskoodi> koulutuskoodit = new HashSet<>();

    public void setKoulutuskoodit(Set<Koulutuskoodi> koulutuskoodit) {
        this.koulutuskoodit.clear();
        this.koulutuskoodit.addAll(koulutuskoodit);
        this.koulutuskoodit.forEach(koulutuskoodi -> koulutuskoodi.setCachedPeruste(this));
    }
}

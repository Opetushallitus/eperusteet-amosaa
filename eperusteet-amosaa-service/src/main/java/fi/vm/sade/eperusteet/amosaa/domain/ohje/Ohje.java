package fi.vm.sade.eperusteet.amosaa.domain.ohje;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Ohje extends AbstractAuditedEntity implements ReferenceableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml
    private String kysymys;

    @ValidHtml
    private String vastaus;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti lokalisoituKysymys;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti lokalisoituVastaus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ohje_koulutustoimija",
            joinColumns = @JoinColumn(name = "ohje_id"),
            inverseJoinColumns = @JoinColumn(name = "koulutustoimija_id"))
    private Set<Koulutustoimija> koulutustoimijat = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    @NotNull
    private KoulutustyyppiToteutus toteutus = KoulutustyyppiToteutus.AMMATILLINEN;
}

package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.JotpaTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Table(name = "opetussuunnitelma")
public class Opetussuunnitelma extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Copyable<Opetussuunnitelma>, HistoriaTapahtuma {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    @NotNull
    private Tila tila = Tila.LUONNOS;

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    @NotNull
    private OpsTyyppi tyyppi = OpsTyyppi.OPS;

    @Getter
    @Setter
    private String perusteDiaarinumero;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private CachedPeruste peruste;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @NotNull
    private LokalisoituTeksti nimi;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti kuvaus;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @NotNull
    private Koulutustoimija koulutustoimija;

    @Getter
    @Setter
    private String suoritustapa;

    @ElementCollection
    @Getter
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    private Set<Kieli> julkaisukielet = new HashSet<>();

    @Getter
    @Setter
    private boolean esikatseltavissa = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date voimaantulo;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    @Column(name = "voimassaolo_loppuu")
    private Date voimassaoloLoppuu;

    @Getter
    @Setter
    private String hyvaksyja;

    @Getter
    @Setter
    private String paatosnumero;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date paatospaivamaara;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Opetussuunnitelma pohja;

    @Setter
    @Getter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "opetussuunnitelma_liite", inverseJoinColumns = {
            @JoinColumn(name = "liite_id")
    }, joinColumns = {
            @JoinColumn(name = "opetussuunnitelma_id")
    })
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Set<Liite> liitteet = new HashSet<>();

    @ElementCollection
    @Getter
    @Setter
    private Set<String> tutkintonimikkeet = new HashSet<>();

    @ElementCollection
    @Getter
    @Setter
    private Set<String> osaamisalat = new HashSet<>();

    @Getter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "opetussuunnitelma", orphanRemoval = true)
    private Set<OpetussuunnitelmaAikataulu> opetussuunnitelmanAikataulut = new HashSet<>();

    @Deprecated
    @Getter
    @Setter
    private String oppilaitosTyyppiKoodiUri;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private JotpaTyyppi jotpatyyppi;

    @NotAudited
    @Getter
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "opetussuunnitelma")
    private Set<Julkaisu> julkaisut = new HashSet<>();

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private KoulutusTyyppi koulutustyyppi;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @Getter
    private List<SisaltoViite> sisaltoviitteet = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    @Column(name="peruste_paivitetty_pvm")
    private Date perustePaivitettyPvm;

    @NotAudited
    @Getter
    @Setter
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "opetussuunnitelma", orphanRemoval = true)
    private List<OsaamisenArvioinninToteutussuunnitelma> osaamisenArvioinninToteutussuunnitelmat = new ArrayList<>();

    public void changeKoulutustoimija(Koulutustoimija kt) {
        this.koulutustoimija = kt;
    }

    public void setKoulutustoimija(Koulutustoimija koulutustoimija) {
        // Noop
    }

    public SisaltoViite getRootViite() {
        return getSisaltoviitteet().stream().filter(sv -> sv.getVanhempi() == null).findFirst().get();
    }

    public void attachLiite(Liite liite) {
        liitteet.add(liite);
    }

    public void removeLiite(Liite liite) {
        liitteet.remove(liite);
    }

    public void setOpetussuunnitelmanAikataulut(List<OpetussuunnitelmaAikataulu> opetussuunnitelmanAikataulut) {
        this.opetussuunnitelmanAikataulut.clear();
        if (opetussuunnitelmanAikataulut != null) {
            this.opetussuunnitelmanAikataulut.addAll(opetussuunnitelmanAikataulut);
        }
    }

    public void setOsaamisenArvioinninToteutussuunnitelmat(List<OsaamisenArvioinninToteutussuunnitelma> osaamisenArvioinninToteutussuunnitelmat) {
        this.osaamisenArvioinninToteutussuunnitelmat.clear();
        if (osaamisenArvioinninToteutussuunnitelmat != null) {
            this.osaamisenArvioinninToteutussuunnitelmat.addAll(osaamisenArvioinninToteutussuunnitelmat.stream()
                    .peek(oat -> oat.setOpetussuunnitelma(this))
                    .collect(java.util.stream.Collectors.toList()));
        }
    }

    @Override
    public Opetussuunnitelma copy(boolean deep) {
        Opetussuunnitelma result = new Opetussuunnitelma();
        result.setPeruste(this.getPeruste());
        result.setTyyppi(this.getTyyppi());
        result.setPerusteDiaarinumero(this.getPerusteDiaarinumero());
        result.setNimi(this.getNimi());
        result.setKuvaus(this.getKuvaus());
        result.changeKoulutustoimija(this.getKoulutustoimija());
        result.setSuoritustapa(this.getSuoritustapa());
        result.setPohja(this.getPohja());
        result.setTila(Tila.LUONNOS);
        result.setKoulutustyyppi(this.getKoulutustyyppi());
        result.setJotpatyyppi(this.jotpatyyppi);
        return result;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.opetussuunnitelma;
    }

    public KoulutusTyyppi getOpsKoulutustyyppi() {
        if (koulutustyyppi != null) {
            return koulutustyyppi;
        }

        if (peruste != null) {
            return peruste.getKoulutustyyppi();
        }

        return null;
    }

    public Date getViimeisinJulkaisuAika() {
        if (CollectionUtils.isNotEmpty(julkaisut)) {
            return julkaisut.stream()
                    .sorted(Comparator.comparing(Julkaisu::getLuotu).reversed())
                    .map(Julkaisu::getLuotu)
                    .findFirst().get();
        }

        return null;
    }
}

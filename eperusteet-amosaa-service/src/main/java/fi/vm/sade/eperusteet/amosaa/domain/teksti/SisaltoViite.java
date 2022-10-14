/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.*;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * @author mikkom
 */
@Slf4j
@Entity
@Audited
@Table(name = "sisaltoviite")
public class SisaltoViite extends AbstractAuditedEntity implements ReferenceableEntity, Serializable, Copyable<SisaltoViite>, HistoriaTapahtuma {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Setter
    @Getter
    private boolean pakollinen;

    @Setter
    @Getter
    private boolean valmis;

    @Setter
    @Getter
    private boolean liikkumaton; // Jos tosi, solmun paikkaa ei voi vaihtaa rakennepuussa

    @Getter
    @Setter
    private Long versio;

    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    private SisaltoTyyppi tyyppi = SisaltoTyyppi.TEKSTIKAPPALE;

    @ManyToOne
    @Getter
    @Setter
    private SisaltoViite vanhempi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    private TekstiKappale tekstiKappale;

    @ManyToOne
    @Getter
    private TekstiKappale pohjanTekstikappale;

    @Getter
    @Setter
    @Column(name = "nayta_pohjan_teksti")
    private boolean naytaPohjanTeksti = true;

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti ohjeteksti;

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti perusteteksti;

    @Getter
    @Setter
    @Column(name = "nayta_perusteen_teksti")
    private boolean naytaPerusteenTeksti;

    // Jos tutkinnon osa on tuotu, opsilla ei ole muuten tietoa perusteen osasta
    // Jos null, käytetään valittua opsia
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private CachedPeruste peruste;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Opetussuunnitelma owner;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Tutkinnonosa tosa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Suorituspolku suorituspolku;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Opintokokonaisuus opintokokonaisuus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Koulutuksenosa koulutuksenosa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private TuvaLaajaAlainenOsaaminen tuvaLaajaAlainenOsaaminen;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private KotoKielitaitotaso kotoKielitaitotaso;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private KotoOpinto kotoOpinto;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private KotoLaajaAlainenOsaaminen kotoLaajaAlainenOsaaminen;

    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<OmaOsaAlue> osaAlueet = new ArrayList<>();

    @Getter
    @Setter
    @Column(updatable = false)
    private Long perusteenOsaId;

    @ManyToOne
    @Getter
    @Setter
    private SisaltoViite linkkiSisaltoViite;

    @OneToMany(mappedBy = "vanhempi", fetch = FetchType.LAZY)
    @OrderColumn
    @Getter
    @Setter
    @BatchSize(size = 100)
    private List<SisaltoViite> lapset = new ArrayList<>();

    public SisaltoViite() {
    }

    public SisaltoViite(Opetussuunnitelma owner) {
        this.owner = owner;
    }

    public SisaltoViite getRoot() {
        SisaltoViite root = this;
        while (root.getVanhempi() != null) {
            root = root.getVanhempi();
        }
        return root;
    }

    public static SisaltoViite createLink(Opetussuunnitelma owner, SisaltoViite original) {
        if (!SisaltoTyyppi.isLinkable(original.getTyyppi())) {
            throw new BusinessRuleViolationException("tyyppi ei linkattava");
        }
        SisaltoViite result = new SisaltoViite(owner);
        result.setTekstiKappale(TekstiKappale.copy(original.getTekstiKappale()));
        result.tyyppi = SisaltoTyyppi.LINKKI;
        result.linkkiSisaltoViite = original;
        return result;
    }

    @Override
    public SisaltoViite copy(boolean deep) {
        return copy(this, deep, true);
    }

    public SisaltoViite copy(boolean deep, boolean copyText) {
        return copy(this, deep, copyText);
    }

    public static SisaltoViite copy(SisaltoViite kopioitava) {
        return copy(kopioitava, true, true);
    }

    public static SisaltoViite copy(SisaltoViite kopioitava, boolean copyChildren) {
        return copy(kopioitava, copyChildren, true);
    }

    public static SisaltoViite copy(SisaltoViite original, boolean copyChildren, boolean copyText) {
        if (original != null) {
            if (SisaltoTyyppi.LINKKI.equals(original.getTyyppi())) {
                SisaltoViite result = SisaltoViite.copy(original.getLinkkiSisaltoViite(), copyChildren, copyText);
                return result;
            }

            SisaltoViite result = new SisaltoViite();
            result.setLiikkumaton(original.isLiikkumaton());
            result.setPakollinen(original.isPakollinen());
            result.setNaytaPerusteenTeksti(original.isNaytaPerusteenTeksti());
            result.setOhjeteksti(original.getOhjeteksti());
            result.setTyyppi(original.getTyyppi());
            result.setPerusteteksti(original.getPerusteteksti());
            result.setTosa(Tutkinnonosa.copy(original.getTosa()));
            result.setSuorituspolku(Suorituspolku.copy(original.getSuorituspolku()));
            result.setOpintokokonaisuus(Opintokokonaisuus.copy(original.getOpintokokonaisuus()));
            result.setKoulutuksenosa(Koulutuksenosa.copy(original.getKoulutuksenosa()));
            result.setTuvaLaajaAlainenOsaaminen(TuvaLaajaAlainenOsaaminen.copy(original.getTuvaLaajaAlainenOsaaminen()));
            result.setKotoKielitaitotaso(KotoKielitaitotaso.copy(original.getKotoKielitaitotaso()));
            result.setKotoOpinto(KotoOpinto.copy(original.getKotoOpinto()));
            result.setKotoLaajaAlainenOsaaminen(KotoLaajaAlainenOsaaminen.copy(original.getKotoLaajaAlainenOsaaminen()));
            result.setPeruste(original.getPeruste());
            result.setPerusteenOsaId(original.getPerusteenOsaId());
            result.setTekstiKappale(TekstiKappale.copy(original.getTekstiKappale()));

            result.setNaytaPohjanTeksti(original.isNaytaPohjanTeksti());
            result.updatePohjanTekstikappale(original.getPohjanTekstikappale());

            if (!copyText
                    && original.getTekstiKappale() != null
                    && original.getTyyppi().equals(SisaltoTyyppi.TEKSTIKAPPALE)) {
                result.getTekstiKappale().setTeksti(null);
                result.updatePohjanTekstikappale(original.getTekstiKappale());
                result.setNaytaPohjanTeksti(true);
            }

            if (copyChildren) {
                result.setLapset(new ArrayList<>());
                for (SisaltoViite lapsi : original.getLapset()) {
                    result.getLapset().add(copy(lapsi, copyChildren, copyText));
                }
            }
            return result;
        }
        else {
            return null;
        }
    }

    static private SisaltoViite createCommon(SisaltoViite parent) {
        SisaltoViite result = new SisaltoViite();
        TekstiKappale tk = new TekstiKappale();
        result.setTekstiKappale(tk);
        result.setVanhempi(parent);
        result.setOwner(parent.getOwner());
        parent.getLapset().add(result);
        return result;
    }

    static public SisaltoViite createTutkinnonOsa(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setLiikkumaton(true);
        result.setTyyppi(SisaltoTyyppi.TUTKINNONOSA);
        Tutkinnonosa tosa = new Tutkinnonosa();
        result.setTosa(tosa);
        return result;
    }

    static public SisaltoViite createTekstikappale(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.TEKSTIKAPPALE);
        return result;
    }

    static public SisaltoViite createOpintokokonaisuus(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.OPINTOKOKONAISUUS);
        Opintokokonaisuus opintokokonaisuus = new Opintokokonaisuus();
        result.setOpintokokonaisuus(opintokokonaisuus);
        return result;
    }

    static public SisaltoViite createKoulutuksenosa(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.KOULUTUKSENOSA);
        Koulutuksenosa koulutuksenosa = new Koulutuksenosa();
        result.setKoulutuksenosa(koulutuksenosa);
        return result;
    }

    static public SisaltoViite createTuvaLaajaAlainenOsaaminen(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.LAAJAALAINENOSAAMINEN);
        TuvaLaajaAlainenOsaaminen tuvaLaajaAlainenOsaaminen = new TuvaLaajaAlainenOsaaminen();
        result.setTuvaLaajaAlainenOsaaminen(tuvaLaajaAlainenOsaaminen);
        return result;
    }

    static public SisaltoViite createKotoKielitaitotaso(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.KOTO_KIELITAITOTASO);
        KotoKielitaitotaso kielitaitotaso = new KotoKielitaitotaso();
        result.setKotoKielitaitotaso(kielitaitotaso);
        return result;
    }

    static public SisaltoViite createKotoOpinto(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.KOTO_OPINTO);
        KotoOpinto kotoOpinto = new KotoOpinto();
        result.setKotoOpinto(kotoOpinto);
        return result;
    }

    static public SisaltoViite createKotoLaajaAlainenOsaaminen(SisaltoViite parent) {
        SisaltoViite result = createCommon(parent);
        result.setTyyppi(SisaltoTyyppi.KOTO_LAAJAALAINENOSAAMINEN);
        KotoLaajaAlainenOsaaminen lao = new KotoLaajaAlainenOsaaminen();
        result.setKotoLaajaAlainenOsaaminen(lao);
        return result;
    }

    public void updatePohjanTekstikappale(TekstiKappale other) {
        this.pohjanTekstikappale = other;
    }

    public Long getPerusteId() {
        if (tosa != null && tosa.getVierastutkinnonosa() != null) {
            return tosa.getVierastutkinnonosa().getPerusteId();
        }

        if (peruste != null) {
            return peruste.getPerusteId();
        }

        return null;
    }


    public boolean isPakollinen() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.pakollinen;
        }
        return pakollinen;
    }

    public boolean isValmis() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.valmis;
        }
        return valmis;
    }

    public boolean isLiikkumaton() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.liikkumaton;
        }
        return liikkumaton;
    }

    public Long getVersio() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.versio;
        }
        return versio;
    }

    public TekstiKappale getTekstiKappale() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.tekstiKappale;
        }
        return tekstiKappale;
    }

    public TekstiKappale getPohjanTekstikappale() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.pohjanTekstikappale;
        }
        return pohjanTekstikappale;
    }

    public boolean isNaytaPohjanTeksti() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.naytaPohjanTeksti;
        }
        return naytaPohjanTeksti;
    }

    public LokalisoituTeksti getOhjeteksti() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.ohjeteksti;
        }
        return ohjeteksti;
    }

    public LokalisoituTeksti getPerusteteksti() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.perusteteksti;
        }
        return perusteteksti;
    }

    public boolean isNaytaPerusteenTeksti() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.naytaPerusteenTeksti;
        }
        return naytaPerusteenTeksti;
    }

    public CachedPeruste getPeruste() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.peruste;
        }
        return peruste;
    }

    public Tutkinnonosa getTosa() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.tosa;
        }
        return tosa;
    }

    public Suorituspolku getSuorituspolku() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.suorituspolku;
        }
        return suorituspolku;
    }

    public Opintokokonaisuus getOpintokokonaisuus() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.opintokokonaisuus;
        }
        return opintokokonaisuus;
    }

    public Koulutuksenosa getKoulutuksenosa() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.koulutuksenosa;
        }
        return koulutuksenosa;
    }

    public TuvaLaajaAlainenOsaaminen getTuvaLaajaAlainenOsaaminen() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.tuvaLaajaAlainenOsaaminen;
        }
        return tuvaLaajaAlainenOsaaminen;
    }

    public KotoKielitaitotaso getKotoKielitaitotaso() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.kotoKielitaitotaso;
        }
        return kotoKielitaitotaso;
    }

    public KotoOpinto getKotoOpinto() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.kotoOpinto;
        }
        return kotoOpinto;
    }

    public KotoLaajaAlainenOsaaminen getKotoLaajaAlainenOsaaminen() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.kotoLaajaAlainenOsaaminen;
        }
        return kotoLaajaAlainenOsaaminen;
    }

    public List<OmaOsaAlue> getOsaAlueet() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.osaAlueet;
        }
        return osaAlueet;
    }

    public Long getPerusteenOsaId() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.perusteenOsaId;
        }
        return perusteenOsaId;
    }

    public List<SisaltoViite> getLapset() {
        if (SisaltoTyyppi.LINKKI.equals(tyyppi)) {
            return this.linkkiSisaltoViite.lapset;
        }
        return lapset;
    }

    @Override
    public Date getLuotu() {
        if (tekstiKappale != null) {
            return tekstiKappale.getLuotu();
        }
        return null;
    }

    @Override
    public Date getMuokattu() {
        if (tekstiKappale != null) {
            return tekstiKappale.getMuokattu();
        }
        return null;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        if (tekstiKappale != null) {
            return tekstiKappale.getNimi();
        }
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.of(String.valueOf(tyyppi));
    }

    public SisaltoTyyppi getLinkattuTyyppi() {
        if (this.linkkiSisaltoViite != null) {
            return this.linkkiSisaltoViite.tyyppi;
        }
        return this.tyyppi;
    }

    public Long getLinkattuPeruste() {
        if (this.linkkiSisaltoViite != null) {
            if (this.linkkiSisaltoViite.tyyppi.equals(SisaltoTyyppi.TUTKINNONOSA)) {
                return this.linkkiSisaltoViite.getOwner().getPeruste().getId();
            }
        }
        return null;
    }

    public Long getLinkattuSisaltoViiteId() {
        if (this.linkkiSisaltoViite != null) {
            return this.linkkiSisaltoViite.getId();
        }
        return null;
    }

    public Long getLinkattuOps() {
        if (this.linkkiSisaltoViite != null) {
            return this.linkkiSisaltoViite.getOwner().getId();
        }
        return null;
    }

    public void asetOsaAlueet(List<OmaOsaAlue> paivitetyt) {
        this.osaAlueet.clear();
        this.osaAlueet.addAll(paivitetyt);
    }

}

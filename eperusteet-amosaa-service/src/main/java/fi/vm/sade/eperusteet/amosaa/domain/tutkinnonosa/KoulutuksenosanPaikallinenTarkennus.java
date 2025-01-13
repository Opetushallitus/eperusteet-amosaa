package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Audited
@Getter
@Setter
@Table(name = "koulutuksenosan_paikallinen_tarkennus")
public class KoulutuksenosanPaikallinenTarkennus extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti tavoitteetKuvaus;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @OrderColumn
    @JoinTable(name = "paikallinen_koulutuksenosa_tavoitteet",
            joinColumns = @JoinColumn(name = "paikallinen_koulutuksenosa_id"),
            inverseJoinColumns = @JoinColumn(name = "tavoite_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @BatchSize(size = 25)
    private List<LokalisoituTeksti> tavoitteet = new ArrayList<>();

    @OneToMany(mappedBy = "paikallinenTarkennus", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @BatchSize(size = 25)
    private List<KoulutuksenosaLaajaalainenOsaaminen> laajaalaisetosaamiset = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti keskeinenSisalto;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti arvioinninKuvaus;

    @OneToMany(mappedBy = "paikallinenTarkennus", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn
    @BatchSize(size = 25)
    private List<KoulutuksenJarjestaja> koulutuksenJarjestajat = new ArrayList<>();

    public void setLaajaalaisetosaamiset(List<KoulutuksenosaLaajaalainenOsaaminen> laajaalaisetosaamiset) {
        this.laajaalaisetosaamiset.clear();

        if (laajaalaisetosaamiset != null) {
            this.laajaalaisetosaamiset.addAll(laajaalaisetosaamiset.stream().map(lao -> {
                lao.setPaikallinenTarkennus(KoulutuksenosanPaikallinenTarkennus.this);
                return lao;
            }).collect(Collectors.toList()));
        }
    }

    public void setKoulutuksenJarjestajat(List<KoulutuksenJarjestaja> koulutuksenJarjestajat) {
        this.koulutuksenJarjestajat.clear();

        if (koulutuksenJarjestajat != null) {
            this.koulutuksenJarjestajat.addAll(koulutuksenJarjestajat.stream().map(lao -> {
                lao.setPaikallinenTarkennus(KoulutuksenosanPaikallinenTarkennus.this);
                return lao;
            }).collect(Collectors.toList()));
        }
    }

    public static KoulutuksenosanPaikallinenTarkennus copy(KoulutuksenosanPaikallinenTarkennus original) {
        if (original != null) {
            KoulutuksenosanPaikallinenTarkennus result = new KoulutuksenosanPaikallinenTarkennus();

            if (original.getTavoitteetKuvaus() != null) {
                result.setTavoitteetKuvaus(LokalisoituTeksti.of(original.getTavoitteetKuvaus().getTeksti()));
            }

            if (!ObjectUtils.isEmpty(original.getLaajaalaisetosaamiset())) {
                result.setLaajaalaisetosaamiset(original.getLaajaalaisetosaamiset().stream()
                        .map(KoulutuksenosaLaajaalainenOsaaminen::copy)
                        .collect(Collectors.toList()));
                result.getLaajaalaisetosaamiset().forEach(lao -> lao.setPaikallinenTarkennus(result));
            }

            if (!ObjectUtils.isEmpty(original.getTavoitteet())) {
                result.setTavoitteet(original.getTavoitteet().stream()
                        .map(lokalisoituTeksti -> LokalisoituTeksti.of(lokalisoituTeksti.getTeksti()))
                        .collect(Collectors.toList()));
            }

            if (original.getKeskeinenSisalto() != null) {
                result.setKeskeinenSisalto(LokalisoituTeksti.of(original.getKeskeinenSisalto().getTeksti()));
            }

            if (original.getArvioinninKuvaus() != null) {
                result.setArvioinninKuvaus(LokalisoituTeksti.of(original.getArvioinninKuvaus().getTeksti()));
            }

            if (!ObjectUtils.isEmpty(original.getKoulutuksenJarjestajat())) {
                result.setKoulutuksenJarjestajat(original.getKoulutuksenJarjestajat().stream()
                        .map(KoulutuksenJarjestaja::copy)
                        .collect(Collectors.toList()));
                result.getKoulutuksenJarjestajat().forEach(kj -> kj.setPaikallinenTarkennus(result));
            }

            return result;
        } else {
            return null;
        }
    }

}

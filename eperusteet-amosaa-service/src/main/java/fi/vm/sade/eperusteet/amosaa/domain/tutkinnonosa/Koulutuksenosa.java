package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusOsanKoulutustyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusOsanTyyppi;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
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
@Table(name = "koulutuksenosa")
public class Koulutuksenosa extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Kooditettu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    private String nimiKoodi;

    @Deprecated
    private Integer laajuusMinimi;

    @Deprecated
    private Integer laajuusMaksimi;

    @Enumerated(EnumType.STRING)
    private KoulutusOsanKoulutustyyppi koulutusOsanKoulutustyyppi;

    @Enumerated(EnumType.STRING)
    private KoulutusOsanTyyppi koulutusOsanTyyppi;

    @Deprecated
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @Deprecated
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @OrderColumn
    @JoinTable(name = "koulutuksenosa_tavoitteet",
            joinColumns = @JoinColumn(name = "koulutuksenosa_id"),
            inverseJoinColumns = @JoinColumn(name = "tavoite_id"))
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @BatchSize(size = 25)
    private List<LokalisoituTeksti> tavoitteet = new ArrayList<>();

    @Deprecated
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti keskeinenSisalto;

    @Deprecated
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti laajaAlaisenOsaamisenKuvaus;

    @Deprecated
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti arvioinninKuvaus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private KoulutuksenosanPaikallinenTarkennus paikallinenTarkennus;

    public static Koulutuksenosa copy(Koulutuksenosa original) {
        if (original != null) {
            Koulutuksenosa result = new Koulutuksenosa();

            if (original.getNimi() != null) {
                result.setNimi(LokalisoituTeksti.of(original.getNimi().getTeksti()));
            }
            result.setNimiKoodi(original.getNimiKoodi());
            result.setLaajuusMinimi(original.getLaajuusMinimi());
            result.setLaajuusMaksimi(original.getLaajuusMaksimi());
            result.setKoulutusOsanKoulutustyyppi(original.getKoulutusOsanKoulutustyyppi());
            result.setKoulutusOsanTyyppi(original.getKoulutusOsanTyyppi());

            if (original.getKuvaus() != null) {
                result.setKuvaus(LokalisoituTeksti.of(original.getKuvaus().getTeksti()));
            }

            if (original.getKeskeinenSisalto() != null) {
                result.setKeskeinenSisalto(LokalisoituTeksti.of(original.getKeskeinenSisalto().getTeksti()));
            }

            if (original.getLaajaAlaisenOsaamisenKuvaus() != null) {
                result.setLaajaAlaisenOsaamisenKuvaus(LokalisoituTeksti.of(original.getLaajaAlaisenOsaamisenKuvaus().getTeksti()));
            }

            if (original.getArvioinninKuvaus() != null) {
                result.setArvioinninKuvaus(LokalisoituTeksti.of(original.getArvioinninKuvaus().getTeksti()));
            }

            if (!ObjectUtils.isEmpty(original.getTavoitteet())) {
                result.setTavoitteet(original.getTavoitteet().stream()
                        .map(lokalisoituTeksti -> LokalisoituTeksti.of(lokalisoituTeksti.getTeksti()))
                        .collect(Collectors.toList()));
            }

            if (original.getPaikallinenTarkennus() != null) {
                result.setPaikallinenTarkennus(KoulutuksenosanPaikallinenTarkennus.copy(original.getPaikallinenTarkennus()));
            }

            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getUri() {
        return nimiKoodi;
    }
}

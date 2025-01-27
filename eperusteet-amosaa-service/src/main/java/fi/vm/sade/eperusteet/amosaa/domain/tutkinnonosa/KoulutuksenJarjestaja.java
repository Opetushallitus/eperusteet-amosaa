package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.io.Serializable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Getter
@Setter
@Table(name = "koulutuksenjarjestaja")
public class KoulutuksenJarjestaja extends AbstractAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String oid;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti url;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @ManyToOne
    private KoulutuksenosanPaikallinenTarkennus paikallinenTarkennus;

    public static KoulutuksenJarjestaja copy(KoulutuksenJarjestaja original) {
        if (original != null) {
            KoulutuksenJarjestaja result = new KoulutuksenJarjestaja();

            if (original.getNimi() != null) {
                result.setNimi(LokalisoituTeksti.of(original.getNimi().getTeksti()));
            }

            if (original.getUrl() != null) {
                result.setUrl(LokalisoituTeksti.of(original.getUrl().getTeksti()));
            }

            if (original.getKuvaus() != null) {
                result.setKuvaus(LokalisoituTeksti.of(original.getKuvaus().getTeksti()));
            }

            return result;
        } else {
            return null;
        }
    }

}

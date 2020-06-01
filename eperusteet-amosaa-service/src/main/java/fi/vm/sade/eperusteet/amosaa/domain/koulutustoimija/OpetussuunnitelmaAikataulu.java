package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.AikatauluTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Table(name = "opetussuunnitelma_aikataulu")
public class OpetussuunnitelmaAikataulu extends AbstractAuditedEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Opetussuunnitelma opetussuunnitelma;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti tavoite;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private AikatauluTapahtuma tapahtuma;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date tapahtumapaiva;

}

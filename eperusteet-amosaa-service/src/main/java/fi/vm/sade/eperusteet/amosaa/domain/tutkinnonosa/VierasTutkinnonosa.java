package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import fi.vm.sade.eperusteet.amosaa.service.util.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Deprecated
@Table(name = "vierastutkinnonosa")
public class VierasTutkinnonosa extends AbstractAuditedEntity implements Serializable, ReferenceableEntity, Copyable<VierasTutkinnonosa> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @NotNull
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private CachedPeruste cperuste;

    @Getter
    @Setter
    @NotNull
    @Column(name = "peruste_id")
    private Long perusteId;

    @Getter
    @Setter
    @NotNull
    @Column(name = "tosa_id")
    private Long tosaId;

    @Override
    public VierasTutkinnonosa copy(boolean deep) {
        return copy(this);
    }

    public static VierasTutkinnonosa copy(VierasTutkinnonosa original) {
        if (original != null) {
            VierasTutkinnonosa result = new VierasTutkinnonosa();
            result.setCperuste(original.getCperuste());
            result.setPerusteId(original.getPerusteId());
            result.setTosaId(original.getTosaId());
            return result;
        }
        else {
            return null;
        }
    }
}

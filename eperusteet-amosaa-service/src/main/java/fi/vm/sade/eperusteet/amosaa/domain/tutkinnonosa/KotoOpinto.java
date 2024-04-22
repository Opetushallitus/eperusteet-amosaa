package fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.ObjectUtils;

@Entity
@Audited
@Getter
@Setter
@Table(name = "koto_opinto")
public class KotoOpinto extends AbstractAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinTable(name = "koto_opinto_taitotasot",
            joinColumns = @JoinColumn(name = "koto_opinto_id"),
            inverseJoinColumns = @JoinColumn(name = "taitotaso_id"))
    private List<KotoTaitotaso> taitotasot = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinTable(name = "koto_opinto_laaja_alaiset_osaamiset",
            joinColumns = @JoinColumn(name = "koto_opinto_id"),
            inverseJoinColumns = @JoinColumn(name = "laaja_alainen_osaaminen_id"))
    private List<KotoTaitotasoLaajaAlainenOsaaminen> laajaAlaisetOsaamiset = new ArrayList<>();

    public static KotoOpinto copy(KotoOpinto original) {
        if (original != null) {
            KotoOpinto result = new KotoOpinto();

            if (!ObjectUtils.isEmpty(original.getTaitotasot())) {
                result.setTaitotasot(original.getTaitotasot().stream()
                        .map(KotoTaitotaso::copy)
                        .collect(Collectors.toList()));
            }

            if (!ObjectUtils.isEmpty(original.getLaajaAlaisetOsaamiset())) {
                result.setLaajaAlaisetOsaamiset(original.getLaajaAlaisetOsaamiset().stream()
                        .map(KotoTaitotasoLaajaAlainenOsaaminen::copy)
                        .collect(Collectors.toList()));
            }

            return result;
        } else {
            return null;
        }
    }
}

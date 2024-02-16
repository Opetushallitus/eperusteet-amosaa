package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fi.vm.sade.eperusteet.amosaa.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "kommentti")
public class Kommentti extends AbstractAuditedReferenceableEntity {
    @Getter
    @Setter
    @NotNull
    @Column(name = "tekstikappaleviite_id")
    private Long tekstikappaleviiteId;

    @Getter
    @Setter
    @NotNull
    @Column(name = "parent_id")
    private Long parentId;

    @Getter
    @Setter
    @NotNull
    private Boolean poistettu;

    @Getter
    @Setter
    @NotNull
    @Column(length = 1024)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @Size(max = 1024, message = "Kommentin maksimipituus on {max} merkkiä")
    private String sisalto;
}

package fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.vm.sade.eperusteet.amosaa.repository.dialect.JsonBType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Immutable
@Table(name = "julkaisu_data")
@TypeDef(name = "jsonb", defaultForType = JsonBType.class, typeClass = JsonBType.class)
public class JulkaisuData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @NotNull
    @Getter
    private int hash;

    @NotNull
    @Getter
    @Setter
    @Type(type = "jsonb")
    @Column(name = "data")
    private ObjectNode data;

    @PrePersist
    void prepersist() {
        hash = data != null ? data.hashCode() : 0;
    }

    public JulkaisuData() {
    }

    public JulkaisuData(ObjectNode data) {
        this.data = data;
    }
}

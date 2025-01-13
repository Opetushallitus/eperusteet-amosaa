package fi.vm.sade.eperusteet.amosaa.domain.liite;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Table(name = "liite")
@NoArgsConstructor
public class Liite implements Serializable {

    @Id
    @Getter
    @Column(updatable = false)
    private UUID id;

    @Getter
    @NotNull
    @Basic(optional = false)
    private String tyyppi;

    @Getter
    @Size(max = 1024)
    private String nimi;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Getter
    @Basic(fetch = FetchType.LAZY, optional = false)
    @Lob
    @NotNull
    private Blob data;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "opetussuunnitelma_liite",
            joinColumns = @JoinColumn(name = "liite_id"),
            inverseJoinColumns = @JoinColumn(name = "opetussuunnitelma_id"))
    @Getter
    private Set<Opetussuunnitelma> opetussuunnitelmat;

    public Liite(String tyyppi, String nimi, Blob data) {
        this.id = UUID.randomUUID();
        this.luotu = new Date();
        this.nimi = nimi;
        this.tyyppi = tyyppi;
        this.data = data;
    }

    public Date getLuotu() {
        return new Date(luotu.getTime());
    }

}

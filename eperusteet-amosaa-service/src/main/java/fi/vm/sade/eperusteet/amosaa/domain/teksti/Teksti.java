package fi.vm.sade.eperusteet.amosaa.domain.teksti;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Teksti implements Serializable {

    public Teksti() {
        //NOP
    }

    public Teksti(Kieli kieli, String teksti) {
        this.kieli = kieli;
        this.teksti = teksti;
    }

    @Column(insertable = true, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Kieli kieli;

    @NotNull
    @Column(columnDefinition = "TEXT", insertable = true, updatable = false)
    private String teksti;

    public Kieli getKieli() {
        return kieli;
    }

    public String getTeksti() {
        return teksti;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.kieli);
        hash = 47 * hash + Objects.hashCode(this.teksti);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Teksti) {
            final Teksti other = (Teksti) obj;
            return this.kieli == other.kieli && Objects.equals(this.teksti, other.teksti);
        }
        return false;
    }

}

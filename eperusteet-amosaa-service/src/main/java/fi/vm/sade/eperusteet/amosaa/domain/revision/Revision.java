package fi.vm.sade.eperusteet.amosaa.domain.revision;

import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;

import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Revision implements Serializable {
    private final Integer numero;
    private final Date pvm;
    private final String muokkaajaOid;
    private final String kommentti;

    public Revision(Integer number, Long timestamp, String muokkaajaOid, String kommentti) {
        this.numero = number;
        this.pvm = new Date(timestamp);
        this.muokkaajaOid = muokkaajaOid;
        this.kommentti = kommentti;
    }
}

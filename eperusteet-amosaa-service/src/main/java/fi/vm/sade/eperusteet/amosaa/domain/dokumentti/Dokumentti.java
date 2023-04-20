package fi.vm.sade.eperusteet.amosaa.domain.dokumentti;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dokumentti")
@Getter
@Setter
public class Dokumentti {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "opetussuunnitelma_id")
    private Long opsId;

    private String luoja;

    @Column(insertable = true, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Kieli kieli;

    @Temporal(TemporalType.TIMESTAMP)
    private Date aloitusaika;

    @Temporal(TemporalType.TIMESTAMP)
    private Date valmistumisaika;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DokumenttiTila tila = DokumenttiTila.EI_OLE;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DokumenttiEdistyminen edistyminen = DokumenttiEdistyminen.TUNTEMATON;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "dokumenttidata")
    private byte[] data;

    @Column(name = "virhekoodi")
    private String virhekoodi;

    @Getter
    @Setter
    @Column(name = "perusteen_sisalto")
    private boolean perusteenSisalto = false;
}

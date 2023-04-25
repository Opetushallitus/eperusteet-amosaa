package fi.vm.sade.eperusteet.amosaa.dto.dokumentti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiEdistyminen;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;

import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DokumenttiDto {
    private Long id;
    private Long opsId;
    private String luoja;
    private Kieli kieli;
    private Date aloitusaika;
    private Date valmistumisaika;
    private DokumenttiTila tila = DokumenttiTila.EI_OLE;
    private DokumenttiEdistyminen edistyminen = DokumenttiEdistyminen.TUNTEMATON;
    private String virhekoodi = "";
    private boolean perusteenSisalto;
    private Boolean julkaisuDokumentti = false;
}

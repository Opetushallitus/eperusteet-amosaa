package fi.vm.sade.eperusteet.amosaa.domain;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoriaTapahtumaAuditointitiedoilla implements HistoriaTapahtuma {

    private Date luotu;
    private Date muokattu;
    private String luoja;
    private String muokkaaja;

    private Long id;
    private LokalisoituTeksti nimi;
    private NavigationType navigationType;

    public HistoriaTapahtumaAuditointitiedoilla(HistoriaTapahtuma historiaTapahtuma) {
        this.luotu = new Date();
        this.muokattu = new Date();
        this.luoja = SecurityUtil.getAuthenticatedPrincipal().getName();
        this.muokkaaja = SecurityUtil.getAuthenticatedPrincipal().getName();

        this.id = historiaTapahtuma.getId();
        this.nimi = historiaTapahtuma.getNimi();
        this.navigationType = historiaTapahtuma.getNavigationType();
    }

    public HistoriaTapahtumaAuditointitiedoilla(Long id, LokalisoituTeksti nimi, NavigationType navigationType) {
        this.luotu = new Date();
        this.muokattu = new Date();

        this.id = id;
        this.nimi = nimi;
        this.navigationType = navigationType;
    }
}

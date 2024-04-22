package fi.vm.sade.eperusteet.amosaa.dto.ohje;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import java.util.Date;
import java.util.Set;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OhjeDto {
    private Long id;
    private String kysymys;
    private String vastaus;
    private LokalisoituTekstiDto lokalisoituKysymys;
    private LokalisoituTekstiDto lokalisoituVastaus;
    private Date muokattu;
    private Set<KoulutustoimijaBaseDto> koulutustoimijat;
    private KoulutustyyppiToteutus toteutus;
}

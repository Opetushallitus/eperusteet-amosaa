package fi.vm.sade.eperusteet.amosaa.dto.peruste;

import fi.vm.sade.eperusteet.utils.dto.utils.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.utils.dto.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Yhdistää arviointiasteikon ja geneerisen arvioinnin jaetuksi rakenteeksi.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arviointi2020Dto {
    private Long id;
    private LokalisoituTekstiDto kohde;
    private Reference arviointiAsteikko;
    private List<OsaamistasonKriteerit2020Dto> osaamistasonKriteerit = new ArrayList<>();
}
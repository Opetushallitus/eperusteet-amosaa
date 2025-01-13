package fi.vm.sade.eperusteet.amosaa.domain.peruste;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutusDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "peruste_cache_koulutuskoodi")
@NoArgsConstructor
public class Koulutuskoodi implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.NONE)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti nimi;
    private String koulutuskoodiArvo;
    private String koulutuskoodiUri;
    private String koulutusalakoodi;
    private String opintoalakoodi;

    @ManyToOne
    @JoinColumn(name = "cached_peruste_id")
    private CachedPeruste cachedPeruste;

    public static Koulutuskoodi of(KoulutusDto koulutusDto) {
        Koulutuskoodi koulutuskoodi = new Koulutuskoodi();
        koulutuskoodi.setKoulutuskoodiArvo(koulutusDto.getKoulutuskoodiArvo());
        koulutuskoodi.setKoulutuskoodiUri(koulutusDto.getKoulutuskoodiUri());
        koulutuskoodi.setKoulutusalakoodi(koulutusDto.getKoulutusalakoodi());
        koulutuskoodi.setOpintoalakoodi(koulutusDto.getOpintoalakoodi());
        koulutuskoodi.setNimi(LokalisoituTeksti.of(koulutusDto.getNimi().getTekstit()));

        return koulutuskoodi;
    }
 }

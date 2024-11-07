package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;

@UtilityClass
public class NavigationNodeMetaUtil {

    private static final List<SisaltoTyyppi> PAIKALLINEN_SISALTO = List.of(SisaltoTyyppi.TEKSTIKAPPALE, SisaltoTyyppi.KOULUTUKSENOSA, SisaltoTyyppi.LAAJAALAINENOSAAMINEN);

    public static void asetaMetaTiedot(NavigationNodeDto navigationNodeDto, SisaltoViiteKevytDto sisaltoviite, PerusteKaikkiDto perusteKaikkiDto) {
        asetaPublicMetaTiedot(navigationNodeDto, sisaltoviite);

        navigationNodeDto.meta("piilotettu", sisaltoviite.isPiilotettu());

        if (PAIKALLINEN_SISALTO.contains(sisaltoviite.getTyyppi()) && sisaltoviite.getPerusteenOsaId() == null && perusteKaikkiDto != null) {
            navigationNodeDto.meta("postfix_label", "sisalto-paikallinen-merkki");
            navigationNodeDto.meta("postfix_tooltip", "paikallisesti-luotu-" + SisaltoTyyppi.TEKSTIKAPPALE.toString().toLowerCase());
        }
    }

    public static void asetaPublicMetaTiedot(NavigationNodeDto navigationNodeDto, SisaltoViiteKevytDto sisaltoviite) {

        if(sisaltoviite.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA) && sisaltoviite.getTosa() != null && sisaltoviite.getTosa().getTyyppi().equals(TutkinnonosaTyyppi.OMA)) {
            navigationNodeDto.meta("postfix_label", "tutkinnon-osa-paikallinen-merkki");
            navigationNodeDto.meta("postfix_tooltip", "paikallisesti-luotu-tutkinnon-osa");
        }
    }

    public static void lisaaTutkinnonOsanOsaAlueet(PerusteKaikkiDto perusteKaikkiDto, SisaltoViiteKevytDto lapsi, NavigationNodeDto node, Predicate<OmaOsaAlueKevytDto> filter) {
        if(!CollectionUtils.isEmpty(lapsi.getOsaAlueet())) {
            if (lapsi.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN))) {
                node.add(NavigationNodeDto.of(NavigationType.pakolliset_osaalueet).meta("navigation-subtype", true));
                node.addAll(lapsi.getOsaAlueet().stream()
                        .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN) && filter.test(osaalue))
                        .map(osaalue -> NavigationNodeDto
                                .of(NavigationType.osaalue, osaAlueNimi(osaalue, perusteKaikkiDto), osaalue.getId())
                                .meta("koodi", osaalue.getKoodiArvo())
                                .meta("sisaltoviiteId", lapsi.getId())
                                .meta("nimi-kieli-filter", true)
                                .meta("piilotettu", osaalue.isPiilotettu())));
            }

            if (lapsi.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN))) {
                node.add(NavigationNodeDto.of(NavigationType.valinnaiset_osaalueet).meta("navigation-subtype", true));
                node.addAll(lapsi.getOsaAlueet().stream()
                        .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN) && filter.test(osaalue))
                        .map(osaalue -> NavigationNodeDto
                                .of(NavigationType.osaalue, osaAlueNimi(osaalue, perusteKaikkiDto), osaalue.getId())
                                .meta("koodi", osaalue.getKoodiArvo())
                                .meta("sisaltoviiteId", lapsi.getId())
                                .meta("nimi-kieli-filter", true)
                                .meta("piilotettu", osaalue.isPiilotettu())));
            }

            if (lapsi.getOsaAlueet().stream().anyMatch(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN))) {
                node.add(NavigationNodeDto.of(NavigationType.paikalliset_osaalueet).meta("navigation-subtype", true));
                node.addAll(lapsi.getOsaAlueet().stream()
                        .filter(osaalue -> osaalue.getTyyppi().equals(OmaOsaAlueTyyppi.PAIKALLINEN) && filter.test(osaalue))
                        .map(osaalue -> NavigationNodeDto
                                .of(NavigationType.osaalue, osaalue.getNimi(), osaalue.getId())
                                .meta("sisaltoviiteId", lapsi.getId())
                                .meta("nimi-kieli-filter", true)));
            }
        }
    }

    private LokalisoituTekstiDto osaAlueNimi(OmaOsaAlueKevytDto osaAlue, PerusteKaikkiDto perusteKaikkiDto) {
        for(TutkinnonosaKaikkiDto tutkinnonosa : perusteKaikkiDto.getTutkinnonOsat()) {
            for(OsaAlueKaikkiDto pOsaAlue: tutkinnonosa.getOsaAlueet()) {
                if (pOsaAlue.getId().equals(osaAlue.getPerusteenOsaAlueId())) {
                    return new LokalisoituTekstiDto(pOsaAlue.getNimi().asMap());
                }
            }
        }
        return osaAlue.getNimi();
    }

}

package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationType;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKevytDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class NavigationUtil {

    private static final Set<NavigationType> NUMEROITAVAT_TYYPIT = Set.of(
            NavigationType.tekstikappale,
            NavigationType.opintokokonaisuus,
            NavigationType.koulutuksenosat,
            NavigationType.koulutuksenosa,
            NavigationType.koto_laajaalainenosaaminen,
            NavigationType.koto_kielitaitotaso,
            NavigationType.koto_opinto);

    private static final Set<KoulutusTyyppi> TUETUT_KOULUTUSTYYPIT = Set.of(
            KoulutusTyyppi.VAPAASIVISTYSTYO, KoulutusTyyppi.VAPAASIVISTYSTYOLUKUTAITO, KoulutusTyyppi.MAAHANMUUTTAJIENKOTOUTUMISKOULUTUS, KoulutusTyyppi.TUTKINTOONVALMENTAVA);

    public static NavigationNodeDto asetaNumerointi(Opetussuunnitelma opetussuunnitelma, NavigationNodeDto node) {
        if (TUETUT_KOULUTUSTYYPIT.contains(opetussuunnitelma.getOpsKoulutustyyppi())){
            asetaNumerointi(node.getChildren(), "");
        }
        return node;
    }

    public static void asetaNumerointi(List<NavigationNodeDto> nodes, String taso) {
        AtomicInteger nro = new AtomicInteger(0);
        nodes.stream()
                .filter(node -> NUMEROITAVAT_TYYPIT.contains(node.getType()))
                .forEach(node -> {
                    node.meta("numerointi", taso + nro.incrementAndGet());
                    asetaNumerointi(node.getChildren(), taso + nro.get() + ".");
                });
    }
}

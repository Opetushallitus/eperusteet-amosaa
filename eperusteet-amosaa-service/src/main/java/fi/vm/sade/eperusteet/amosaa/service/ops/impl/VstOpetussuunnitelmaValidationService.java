package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.dto.NavigationNodeDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaValidationService;
import fi.vm.sade.eperusteet.amosaa.service.util.ValidationCategory;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class VstOpetussuunnitelmaValidationService implements OpetussuunnitelmaValidationService {

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    public static final String SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA = "sisallossa-opintokokonaisuuksia-ilman-nimea";

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.VAPAASIVISTYSTYO, KoulutusTyyppi.VAPAASIVISTYSTYOLUKUTAITO);
    }

    @Override
    public List<Validointi> validoi(@P("ktId") Long ktId, @P("opsId") Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        Validointi opsValidointi = new Validointi(ValidationCategory.OPETUSSUUNNITELMA);
        ValidointiServiceImpl.validoiTiedot(opsValidointi, ops);
        SisaltoViite root = sisaltoviiteRepository.findOneRoot(ops);

        Validointi sisaltoValidointi = new Validointi(ValidationCategory.SISALTO);
        validoi(sisaltoValidointi, root, ops);

        return Arrays.asList(opsValidointi, sisaltoValidointi);
    }

    private void validoi(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        if (viite == null) {
            return;
        }

        if (viite.getTyyppi().equals(SisaltoTyyppi.TEKSTIKAPPALE)) {
            LokalisoituTeksti nimi = viite.getTekstiKappale() != null ? viite.getTekstiKappale().getNimi() : null;

            // Validointi jos osa on pakollinen tai kyseessä itse määritetty sisältö millä ei ole alisisältöä
            if ((viite.getPerusteteksti() == null && viite.getPohjanTekstikappale() == null)) {
                if (viite.getLapset().isEmpty() && viite.getTekstiKappale() != null) {
                    LokalisoituTeksti.validoi(validointi, ops, viite.getTekstiKappale().getTeksti(), NavigationNodeDto.of(viite));
                }
            }
        }

        if (viite.getTyyppi().equals(SisaltoTyyppi.OPINTOKOKONAISUUS)) {
            if (validointi.getVirheet().stream().map(Validointi.Virhe::getKuvaus).noneMatch(kuvaus -> kuvaus.equals(SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA))
                && (viite.getTekstiKappale().getNimi() == null
                    || viite.getTekstiKappale().getNimi().getTeksti().isEmpty())) {
                validointi.virhe(SISALLOSSA_NIMETTOMIA_OPINTOKOKONAISUUKSIA, NavigationNodeDto.of(viite));
            }
            if (hasEmptyLaajuus(viite)) {
                validointi.huomautukset("sisallossa-opintokokonaisuuksia-ilman-laajuutta", NavigationNodeDto.of(viite));
            }
            if (!hasEmptyLaajuus(viite) && viite.getOpintokokonaisuus().getLaajuusYksikko() == null) {
                validointi.virhe("sisallossa-opintokokonaisuuksia-ilman-laajuusyksikkoa", NavigationNodeDto.of(viite));
            }
        }

        if (viite.getLapset() != null) {
            for (SisaltoViite lapsi : viite.getLapset()) {
                validoi(validointi, lapsi, ops);
            }
        }
    }

    private boolean hasEmptyLaajuus(SisaltoViite viite) {
        return viite.getOpintokokonaisuus().getLaajuus() == null || viite.getOpintokokonaisuus().getLaajuus().compareTo(BigDecimal.ZERO) == 0;
    }

}

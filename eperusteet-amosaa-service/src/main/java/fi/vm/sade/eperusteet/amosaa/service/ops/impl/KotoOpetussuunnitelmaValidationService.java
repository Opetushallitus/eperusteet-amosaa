package fi.vm.sade.eperusteet.amosaa.service.ops.impl;


import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaValidationService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

@Service
public class KotoOpetussuunnitelmaValidationService implements OpetussuunnitelmaValidationService {

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.MAAHANMUUTTAJIENKOTOUTUMISKOULUTUS);
    }

    @Override
    public Validointi validoi(@P("ktId") Long ktId, @P("opsId") Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        Validointi validointi = new Validointi();
        ValidointiServiceImpl.validoiTiedot(validointi, ops);
        SisaltoViite root = sisaltoviiteRepository.findOneRoot(ops);
        validoi(validointi, root, ops);

        return validointi;
    }

    private void validoi(Validointi validointi, SisaltoViite viite, Opetussuunnitelma ops) {
        if (viite == null || viite.getLapset() == null) {
            return;
        }

        if (viite.getTekstiKappale() != null && viite.getPerusteenOsaId() == null) {
            LokalisoituTeksti.validoi(validointi, ops, viite.getTekstiKappale().getTeksti(), viite.getTekstiKappale().getNimi());
        }

        for (SisaltoViite lapsi : viite.getLapset()) {
            if (lapsi == null || !lapsi.getTyyppi().equals(SisaltoTyyppi.TEKSTIKAPPALE)) {
                continue;
            }

            validoi(validointi, lapsi, ops);
        }
    }
}

package fi.vm.sade.eperusteet.amosaa.service.peruste.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaSisaltoCreateService;
import fi.vm.sade.eperusteet.amosaa.service.peruste.OpetussuunnitelmaPerustePaivitysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class MuutOpetussuunnitelmaPerustePaivitysService implements OpetussuunnitelmaPerustePaivitysService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private OpetussuunnitelmaSisaltoCreateService opetussuunnitelmaSisaltoCreateService;

    @Override
    public Set<KoulutusTyyppi> getTyypit() {
        return Sets.newHashSet(KoulutusTyyppi.VAPAASIVISTYSTYO, KoulutusTyyppi.VAPAASIVISTYSTYOLUKUTAITO, KoulutusTyyppi.MAAHANMUUTTAJIENKOTOUTUMISKOULUTUS);
    }

    @Override
    public void paivitaOpetussuunnitelma(Long opetussuunnitelmaId, PerusteKaikkiDto perusteKaikkiDto) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opetussuunnitelmaId);
        for (PerusteenOsaViiteDto.Laaja lapsi : perusteKaikkiDto.getSisalto().getLapset()) {
            opetussuunnitelmaSisaltoCreateService.paivitaOpetussuunnitelmaPerusteenSisallolla(opetussuunnitelma, opetussuunnitelma.getRootViite(), lapsi, perusteKaikkiDto.getSisalto());
        }

        opetussuunnitelmaSisaltoCreateService.poistaPerusteenSisaltoOpetussuunnitelmalta(opetussuunnitelma, opetussuunnitelma.getRootViite(), perusteKaikkiDto);
    }

}

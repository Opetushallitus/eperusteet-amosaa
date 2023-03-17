package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.KotoTaitotaso;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Koulutuksenosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusArviointi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTavoite;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TuvaLaajaAlainenOsaaminen;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoKielitaitotasoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoOpintoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutuksenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OpintokokonaisuusDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteenOsaViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TekstiKappaleDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TuvaLaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaSisaltoCreateService;
import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class OpetussuunnitelmaSisaltoCreateServiceImpl implements OpetussuunnitelmaSisaltoCreateService {

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private DtoMapper mapper;

    @Override
    public void poistaPerusteenSisaltoOpetussuunnitelmalta(Opetussuunnitelma opetussuunnitelma, SisaltoViite parentViite, PerusteKaikkiDto perusteKaikkiDto) {
        Map<Long, PerusteenOsaViiteDto.Laaja> perusteenOsat = CollectionUtil.treeToStream(perusteKaikkiDto.getSisalto(), PerusteenOsaViiteDto.Laaja::getLapset)
                .filter(osa -> osa.getPerusteenOsa() != null)
                .collect(Collectors.toMap(osa -> osa.getPerusteenOsa().getId(), osa -> osa));

        if (CollectionUtils.isNotEmpty(parentViite.getLapset())) {
            Set<SisaltoViite> poistettavatViitteet = new HashSet<>();
            parentViite.getLapset().forEach(lapsi -> {
                if (lapsi != null) {
                    if (!lapsi.getTyyppi().equals(SisaltoTyyppi.LINKKI) && lapsi.getPerusteenOsaId() != null && !perusteenOsat.containsKey(lapsi.getPerusteenOsaId())) {
                        poistettavatViitteet.add(lapsi);
                    }

                    poistaPerusteenSisaltoOpetussuunnitelmalta(opetussuunnitelma, lapsi, perusteKaikkiDto);
                }
            });

            for(SisaltoViite poistettavaLapsi : poistettavatViitteet) {
                poistettavaLapsi.getLapset().forEach(lisattavaLapsi ->  {
                    lisattavaLapsi.setVanhempi(parentViite);
                    sisaltoviiteRepository.save(parentViite);
                });
                parentViite.getLapset().addAll(poistettavaLapsi.getLapset());

                poistettavaLapsi.getLapset().clear();
                parentViite.getLapset().remove(poistettavaLapsi);
                sisaltoviiteRepository.delete(poistettavaLapsi);
            }

            sisaltoviiteRepository.save(parentViite);
        }
    }

    @Override
    public void paivitaOpetussuunnitelmaPerusteenSisallolla(Opetussuunnitelma opetussuunnitelma, SisaltoViite parentViite, PerusteenOsaViiteDto.Laaja sisalto) {
        Map<Long, SisaltoViite> svPerusteelta = CollectionUtil.treeToStream(opetussuunnitelma.getSisaltoviitteet(), SisaltoViite::getLapset)
                .filter(sv -> sv.getPerusteenOsaId() != null)
                .collect(Collectors.toMap(SisaltoViite::getPerusteenOsaId, sv -> sv));

        SisaltoViite sisaltoviite = svPerusteelta.get(sisalto.getPerusteenOsa().getId());

        if (sisaltoviite == null) {
            sisaltoviite = alustaSisaltoviite(parentViite, sisalto);
        }

        for (PerusteenOsaViiteDto.Laaja lapsi : sisalto.getLapset()) {
            paivitaOpetussuunnitelmaPerusteenSisallolla(opetussuunnitelma, sisaltoviite, lapsi);
        }
    }

    @Override
    public void alustaOpetussuunnitelmaPerusteenSisallolla(Opetussuunnitelma opetussuunnitelma, SisaltoViite parentViite, PerusteenOsaViiteDto.Laaja sisalto) {

        SisaltoViite sisaltoviite = alustaSisaltoviite(parentViite, sisalto);

        for (PerusteenOsaViiteDto.Laaja lapsi : sisalto.getLapset()) {
            alustaOpetussuunnitelmaPerusteenSisallolla(opetussuunnitelma, sisaltoviite, lapsi);
        }
    }

    private SisaltoViite alustaSisaltoviite(SisaltoViite parentViite, PerusteenOsaViiteDto.Laaja sisalto) {
        SisaltoViite sisaltoviite = null;

        if (sisalto.getPerusteenOsa() instanceof TekstiKappaleDto) {
            sisaltoviite = SisaltoViite.createTekstikappale(parentViite);
            sisaltoviite.setPerusteteksti(LokalisoituTeksti.of(((TekstiKappaleDto) sisalto.getPerusteenOsa()).getTeksti()));
            sisaltoviite.setNaytaPerusteenTeksti(true);
        }

        if (sisalto.getPerusteenOsa() instanceof OpintokokonaisuusDto) {
            sisaltoviite = SisaltoViite.createOpintokokonaisuus(parentViite);
            OpintokokonaisuusDto opintokokonaisuusDto = (OpintokokonaisuusDto) sisalto.getPerusteenOsa();
            sisaltoviite.getOpintokokonaisuus().setTyyppi(OpintokokonaisuusTyyppi.PERUSTEESTA);
            sisaltoviite.getOpintokokonaisuus().setKuvaus(LokalisoituTeksti.of(opintokokonaisuusDto.getKuvaus()));
            sisaltoviite.getOpintokokonaisuus().setMinimilaajuus(opintokokonaisuusDto.getMinimilaajuus());
            sisaltoviite.getOpintokokonaisuus().setOpetuksenTavoiteOtsikko(LokalisoituTeksti.of(opintokokonaisuusDto.getOpetuksenTavoiteOtsikko()));
            sisaltoviite.getOpintokokonaisuus().setNimiKoodi(opintokokonaisuusDto.getNimiKoodi().getUri());
            sisaltoviite.getOpintokokonaisuus().setArvioinnit(opintokokonaisuusDto.getArvioinnit().stream()
                    .map(arviointi -> new OpintokokonaisuusArviointi(true, LokalisoituTeksti.of(arviointi))).collect(Collectors.toList()));
            sisaltoviite.getOpintokokonaisuus().setTavoitteet(opintokokonaisuusDto.getOpetuksenTavoitteet().stream()
                    .map(tavoite -> new OpintokokonaisuusTavoite(true, tavoite.getUri())).collect(Collectors.toList()));
        }

        if (sisalto.getPerusteenOsa() instanceof KoulutuksenOsaDto) {
            sisaltoviite = SisaltoViite.createKoulutuksenosa(parentViite);
            Koulutuksenosa koulutuksenosa = sisaltoviite.getKoulutuksenosa();
            KoulutuksenOsaDto koulutuksenOsaDto = (KoulutuksenOsaDto) sisalto.getPerusteenOsa();
            mapper.map(koulutuksenOsaDto, koulutuksenosa);
            sisaltoviite.getKoulutuksenosa().setId(null);
        }

        if (sisalto.getPerusteenOsa() instanceof TuvaLaajaAlainenOsaaminenDto) {
            sisaltoviite = SisaltoViite.createTuvaLaajaAlainenOsaaminen(parentViite);
            TuvaLaajaAlainenOsaaminen tuvaLaajaAlainenOsaaminen = sisaltoviite.getTuvaLaajaAlainenOsaaminen();
            TuvaLaajaAlainenOsaaminenDto tuvaLaajaAlainenOsaaminenDto = (TuvaLaajaAlainenOsaaminenDto) sisalto.getPerusteenOsa();
            mapper.map(tuvaLaajaAlainenOsaaminenDto, tuvaLaajaAlainenOsaaminen);

            sisaltoviite.setPerusteteksti(LokalisoituTeksti.of(tuvaLaajaAlainenOsaaminenDto.getTeksti()));
            sisaltoviite.setNaytaPerusteenTeksti(true);

            sisaltoviite.getTuvaLaajaAlainenOsaaminen().setId(null);
            sisaltoviite.getTuvaLaajaAlainenOsaaminen().setTeksti(null);
        }

        if (sisalto.getPerusteenOsa() instanceof KotoKielitaitotasoDto) {
            sisaltoviite = SisaltoViite.createKotoKielitaitotaso(parentViite);
            KotoKielitaitotasoDto kotoKielitaitotasoDto = (KotoKielitaitotasoDto) sisalto.getPerusteenOsa();
            sisaltoviite.getKotoKielitaitotaso().setTaitotasot(kotoKielitaitotasoDto.getTaitotasot().stream()
                    .map(perusteenTaitotaso -> KotoTaitotaso.of(perusteenTaitotaso.getNimi().getUri()))
                    .collect(Collectors.toList()));
        }

        if (sisalto.getPerusteenOsa() instanceof KotoOpintoDto) {
            sisaltoviite = SisaltoViite.createKotoOpinto(parentViite);
            KotoOpintoDto kotoOpintoDto = (KotoOpintoDto) sisalto.getPerusteenOsa();
            sisaltoviite.getKotoOpinto().setTaitotasot(kotoOpintoDto.getTaitotasot().stream()
                    .map(perusteenTaitotaso -> KotoTaitotaso.of(perusteenTaitotaso.getNimi().getUri()))
                    .collect(Collectors.toList()));
        }

        if (sisalto.getPerusteenOsa() instanceof KotoLaajaAlainenOsaaminenDto) {
            sisaltoviite = SisaltoViite.createKotoLaajaAlainenOsaaminen(parentViite);
        }

        if (sisaltoviite == null) {
            throw new BusinessRuleViolationException("vaara-sisaltoviite-tyyppi");
        }

        sisaltoviite.setPerusteenOsaId(sisalto.getPerusteenOsa().getId());
        sisaltoviite.getTekstiKappale().setNimi(LokalisoituTeksti.of(sisalto.getPerusteenOsa().getNimi()));
        sisaltoviiteRepository.save(sisaltoviite);
        return sisaltoviite;
    }
}

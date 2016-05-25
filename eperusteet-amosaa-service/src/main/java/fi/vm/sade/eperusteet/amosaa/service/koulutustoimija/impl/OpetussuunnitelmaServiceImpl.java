/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.impl;

import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttaja;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.Kayttajaoikeus;
import fi.vm.sade.eperusteet.amosaa.domain.kayttaja.KayttajaoikeusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.revision.Revision;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author nkala
 */
@Service
@Transactional
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private KayttajaoikeusRepository kayttajaoikeusRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private TekstiKappaleRepository tkRepository;

    @Autowired
    private SisaltoviiteRepository tkvRepository;

    @Autowired
    private SisaltoViiteService tkvService;

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Override
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(Long ktId) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        List<Opetussuunnitelma> opsit = repository.findAllByKoulutustoimija(koulutustoimija);
        return mapper.mapAsList(opsit, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getOpetussuunnitelmatByTyyppi(OpsTyyppi tyyppi) {
        List<Opetussuunnitelma> opetussuunnitelmat = repository.findAllByTyyppi(tyyppi);
        return mapper.mapAsList(opetussuunnitelmat, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public List<OpetussuunnitelmaBaseDto> getPohjat() {
        List<Opetussuunnitelma> opetussuunnitelmat = repository.findAllByTyyppiAndTila(OpsTyyppi.POHJA, Tila.JULKAISTU);
        return mapper.mapAsList(opetussuunnitelmat, OpetussuunnitelmaBaseDto.class);
    }

    private void alustaOpetussuunnitelma(Opetussuunnitelma ops, SisaltoViite rootTkv) {
        // Lisätään tutkinnonosille oma sisältöviite
        {
            SisaltoViite tosat = new SisaltoViite();
            TekstiKappale tk = new TekstiKappale();
            tk.setNimi(LokalisoituTeksti.of(Kieli.FI, "Tutkinnon osat"));
            tk.setValmis(true);
            tosat.setTekstiKappale(tkRepository.save(tk));
            tosat.setLiikkumaton(true);
            tosat.setVanhempi(rootTkv);
            tosat.setPakollinen(true);
            tosat.setTyyppi(SisaltoTyyppi.TUTKINNONOSAT);
            tosat.setOwner(ops);
            rootTkv.getLapset().add(tkvRepository.save(tosat));
        }

        // Lisätään suorituspoluille oma sisältöviite
        if (ops.getTyyppi() == OpsTyyppi.OPS) {
            SisaltoViite suorituspolut = new SisaltoViite();
            TekstiKappale tk = new TekstiKappale();
            tk.setNimi(LokalisoituTeksti.of(Kieli.FI, "Suorituspolut"));
            tk.setValmis(true);
            suorituspolut.setTekstiKappale(tkRepository.save(tk));
            suorituspolut.setPakollinen(true);
            suorituspolut.setTyyppi(SisaltoTyyppi.SUORITUSPOLUT);
            suorituspolut.setOwner(ops);
            suorituspolut.setLiikkumaton(true);
            suorituspolut.setVanhempi(rootTkv);
            rootTkv.getLapset().add(tkvRepository.save(suorituspolut));
        }
    }

    private void setOpsCommon(Opetussuunnitelma ops, PerusteDto peruste, SisaltoViite rootTkv) {
        if (peruste == null) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }

        CachedPeruste cperuste = cachedPerusteRepository.findOneByDiaarinumeroAndLuotu(peruste.getDiaarinumero(), peruste.getGlobalVersion().getAikaleima());
        if (cperuste == null) {
            cperuste = new CachedPeruste();
            cperuste.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
            cperuste.setDiaarinumero(peruste.getDiaarinumero());
            cperuste.setLuotu(peruste.getGlobalVersion().getAikaleima());
            cperuste.setPeruste(ops.getTyyppi() == OpsTyyppi.YLEINEN
                    ? eperusteetService.getYleinenPohjaSisalto()
                    : eperusteetService.getPerusteData(peruste.getId()));
            cperuste = cachedPerusteRepository.save(cperuste);
        }

        ops.setPeruste(cperuste);
        alustaOpetussuunnitelma(ops, rootTkv);

        List<TutkinnonOsaKaikkiDto> tutkinnonOsat = eperusteetService.getPerusteSisalto(cperuste, PerusteKaikkiDto.class).getTutkinnonOsat();
        SisaltoViite tosat = rootTkv.getLapset().get(0);
        for (TutkinnonOsaKaikkiDto tosa : tutkinnonOsat) {
            SisaltoViite uusi = SisaltoViite.createTutkinnonOsa(tosat);
            uusi.setPakollinen(true);
            uusi.getTekstiKappale().setNimi(LokalisoituTeksti.of(tosa.getNimi()));
            Tutkinnonosa uusiTosa = uusi.getTosa();
            uusiTosa.setTyyppi(TutkinnonosaTyyppi.PERUSTEESTA);
            uusiTosa.setPerusteentutkinnonosa(tosa.getId());
            uusiTosa.setKoodi(tosa.getKoodiUri());
            tkvRepository.save(uusi);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public OpetussuunnitelmaBaseDto addOpetussuunnitelma(Long ktId, OpetussuunnitelmaDto opsDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = mapper.map(opsDto, Opetussuunnitelma.class);
        ops.setKoulutustoimija(kt);
        ops.setTila(Tila.LUONNOS);
        ops = repository.save(ops);

        SisaltoViite rootTkv = null;
        if (opsDto.getTyyppi() != OpsTyyppi.YHTEINEN) {
            rootTkv = new SisaltoViite();
            rootTkv.setOwner(ops);
            rootTkv = tkvRepository.save(rootTkv);
        }

        if (kt.isOph()) {
            opsDto.setTyyppi(OpsTyyppi.POHJA);
        }
        else {
            switch (opsDto.getTyyppi()) {
                case OPS:
                    PerusteDto peruste = eperusteetService.getPeruste(opsDto.getPerusteDiaarinumero(), PerusteDto.class);
                    setOpsCommon(ops, peruste, rootTkv);
                    break;
                case YLEINEN:
                    PerusteDto yleinen = eperusteetService.getYleinenPohja();
                    setOpsCommon(ops, yleinen, rootTkv);
                    break;
                case YHTEINEN:
                    Opetussuunnitelma pohja = repository.findOne(opsDto.getPohja().getIdLong());
                    if (pohja == null) {
                        throw new BusinessRuleViolationException("pohjaa-ei-loytynyt");
                    }
                    else if (pohja.getTila() != Tila.JULKAISTU) {
                        throw new BusinessRuleViolationException("vain-julkaistua-pohjaa-voi-kayttaa");
                    }

                    SisaltoViite pohjatkv = tkvRepository.findOneRoot(pohja);
                    tkvRepository.save(tkvService.kopioiHierarkia(pohjatkv, ops));
                    ops.setPohja(pohja);
                    break;
                case POHJA:
                    throw new BusinessRuleViolationException("ainoastaan-oph-voi-tehda-pohjia");
                default:
                    throw new BusinessRuleViolationException("opstyypille-ei-toteutusta");
            }
        }
        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public OpetussuunnitelmaDto getOpetussuunnitelma(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public List<PoistettuDto> getPoistetut(Long ktId, Long id) {
        ArrayList<PoistettuDto> result = new ArrayList<>();
        return result;
    }

    @Override
    public OpetussuunnitelmaDto update(Long ktId, Long opsId, OpetussuunnitelmaDto body) {
        Opetussuunnitelma ops = repository.findOne(opsId);
//        body.setPeruste(mapper.map(ops.getPeruste(), CachedPerusteBaseDto.class));
        body.setId(opsId);
        body.setTila(ops.getTila());
        repository.setRevisioKommentti(body.getKommentti());
        Opetussuunnitelma updated = mapper.map(body, ops);
        return mapper.map(updated, OpetussuunnitelmaDto.class);
    }

    @Override
    public KayttajaoikeusDto updateOikeus(Long ktId, Long opsId, Long kayttajaId, KayttajaoikeusDto oikeusDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);

        // FIXME lisää oikeustarkistelu muokkaajalle

        Kayttajaoikeus oikeus = kayttajaoikeusRepository.findOneByKayttajaAndOpetussuunnitelma(kayttaja, ops);

        if (oikeusDto.getOikeus() == KayttajaoikeusTyyppi.LUKU) {
            if (oikeus != null) {
                kayttajaoikeusRepository.delete(oikeus);
            }
            return null;
        }

        if (oikeus == null) {
            oikeus = new Kayttajaoikeus();
            oikeus.setKoulutustoimija(kt);
            oikeus.setKayttaja(kayttaja);
            oikeus.setOpetussuunnitelma(ops);
        }

        oikeus.setOikeus(oikeusDto.getOikeus());
        oikeus = kayttajaoikeusRepository.save(oikeus);
        return mapper.map(oikeus, KayttajaoikeusDto.class);
    }

    @Override
    public List<KayttajaoikeusDto> getOikeudet(Long ktId, Long opsId) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        List<KayttajaoikeusDto> oikeusDtot = mapper.mapAsList(kayttajaoikeusRepository.findAllByKoulutustoimijaAndOpetussuunnitelma(kt, ops), KayttajaoikeusDto.class);
        return oikeusDtot;
    }

    @Override
    public List<Revision> getRevisions(Long ktId, Long opsId) {
        return repository.getRevisions(opsId);
    }

    @Override
    public Revision getLatestRevision(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }

    @Override
    public Integer getLatestRevisionId(Long ktId, Long opsId) {
        return repository.getLatestRevisionId(opsId);
        }

    @Override
    public Object getData(Long ktId, Long opsId, Integer rev) {
        return mapper.map(repository.findRevision(opsId, rev), OpetussuunnitelmaDto.class);
    }

    @Override
    public Revision getRemoved(Long ktId, Long opsId) {
        return repository.getLatestRevision(opsId);
    }

    @Override
    public OpetussuunnitelmaBaseDto updateTila(Long ktId, Long opsId, Tila tila) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Tila nykyinen = ops.getTila();
        if (nykyinen.mahdollisetSiirtymat().contains(tila)) {
            ops.setTila(tila);
        }
        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }
}

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
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
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.PoistettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.kayttaja.KayttajaoikeusDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.*;
import fi.vm.sade.eperusteet.amosaa.dto.ops.VanhentunutPohjaperusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioHistoriaLiitosDto;
import fi.vm.sade.eperusteet.amosaa.dto.organisaatio.OrganisaatioStatus;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.kayttaja.KayttajaoikeusRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetClient;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author nkala
 */
@Service
@Slf4j
@Transactional
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {
    static private final Logger LOG = LoggerFactory.getLogger(OpetussuunnitelmaServiceImpl.class);

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private EperusteetClient eperusteetClient;

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
    private ValidointiService validointiService;

    @Autowired
    private KayttajanTietoService ktService;

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    private DokumenttiService dokumenttiService;

    private KoulutustoimijaService koulutustoimijaService;
    
    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;
    
    @Autowired
    private OrganisaatioService organisaatioService;
    

    @Autowired
    public void setKoulutustoimijaService(KoulutustoimijaService kts) {
        this.koulutustoimijaService = kts;
    }

    public KoulutustoimijaService getKoulutustoimijaService() {
        return this.koulutustoimijaService;
    }

    @Autowired
    private LocalizedMessagesService messages;

    private ObjectMapper objMapper;

    @PostConstruct
    protected void init() {
        objMapper = new ObjectMapper();
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        objMapper.registerModule(module);
        objMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Override
    @Transactional
    public void mapPerusteIds() {
        List<CachedPeruste> cperusteet = cachedPerusteRepository.findAll();
        for (CachedPeruste cperuste : cperusteet) {
            if (cperuste.getPerusteId() == null) {
                try {
                    JsonNode peruste = eperusteetService.getPerusteSisalto(cperuste, JsonNode.class);
                    cperuste.setPerusteId(peruste.get("id").asLong());
                } catch (Exception ex) {
                    LOG.error(ex.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    @Transactional
    public void mapKoulutustyyppi() {
        List<CachedPeruste> cperusteet = cachedPerusteRepository.findAll();
        for (CachedPeruste cperuste : cperusteet) {
            if (cperuste.getKoulutustyyppi() == null) {
                try {
                    JsonNode peruste = eperusteetService.getPerusteSisalto(cperuste, JsonNode.class);
                    cperuste.setKoulutustyyppi(KoulutusTyyppi.of(peruste.get("koulutustyyppi").asText()));
                } catch (Exception ex) {
                    LOG.error(ex.getLocalizedMessage());
                }
            }
        }
    }

    public void mapKoulutukset() {
        List<CachedPeruste> cperusteet = cachedPerusteRepository.findAll();
        for (CachedPeruste cperuste : cperusteet) {
            if (cperuste.getKoulutukset() == null) {
                try {
                    JsonNode peruste = eperusteetService.getPerusteSisalto(cperuste, JsonNode.class);
                    Set<KoulutusDto> koulutukset = objMapper.readValue(peruste.get("koulutukset").toString(),
                            objMapper.getTypeFactory().constructCollectionType(Set.class, KoulutusDto.class));
                    cperuste.setKoulutukset(koulutukset);
                } catch (Exception ex) {
                    LOG.error(ex.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public List<OpetussuunnitelmaDto> getJulkisetOpetussuunnitelmat(Long ktId) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        List<Opetussuunnitelma> opsit = repository.findAllByKoulutustoimijaAndTila(koulutustoimija, Tila.JULKAISTU);
        return mapper.mapAsList(opsit, OpetussuunnitelmaDto.class);
    }

    @Override
    public Page<OpetussuunnitelmaBaseDto> getOpetussuunnitelmat(
            Long ktId,
            PageRequest page,
            OpsHakuDto query
    ) {
        Koulutustoimija koulutustoimija = koulutustoimijaRepository.findOne(ktId);
        if (koulutustoimija != null && query != null) {
            query.setKoulutustoimija(koulutustoimija.getId());
        }
        return repository.findBy(page, query)
                .map(ops -> mapper.map(ops, OpetussuunnitelmaBaseDto.class));
    }

    @Override
    public <T extends OpetussuunnitelmaBaseDto> List<T> getJulkaistutPerusteenOpetussuunnitelmat(String diaari, Class<T> type) {
        List<Opetussuunnitelma> opsit = repository.findAllJulkaistutByPerusteDiaarinumero(diaari);
        return mapper.mapAsList(opsit, type);
    }

    @Override
    public <T extends OpetussuunnitelmaBaseDto> List<T> getPerusteenOpetussuunnitelmat(String diaari, Class<T> type) {
        List<Opetussuunnitelma> opsit = repository.findAllByPerusteDiaarinumero(diaari);
        return mapper.mapAsList(opsit, type);
    }

    @Override
    public JsonNode getOpetussuunnitelmanPeruste(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        return eperusteetService.getPerusteSisalto(ops.getPeruste().getId(), JsonNode.class);
    }

    @Override
    public List<OpetussuunnitelmaDto> getOtherOpetussuunnitelmat(Long ktId) {
        Kayttaja kayttaja = kayttajaRepository.findOneByOid(ktService.getUserOid());
        Koulutustoimija omaKoulutustoimija = koulutustoimijaRepository.findOne(ktId);
        List<Kayttajaoikeus> oikeudet = kayttajaoikeusRepository.findAllByKayttaja(kayttaja);
        return oikeudet.stream()
                .map(Kayttajaoikeus::getOpetussuunnitelma)
                .filter(ops -> ops.getKoulutustoimija().getYstavat().contains(omaKoulutustoimija)
                        && omaKoulutustoimija.getYstavat().contains(ops.getKoulutustoimija()))
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OpetussuunnitelmaDto> getOpetussuunnitelmatOrganisaatioista(String organisaatioId) {
        return repository.findByKoulutustoimijaOrganisaatio(organisaatioId).stream()
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class))
                .collect(Collectors.toList());
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


            // Haetaan perusteen koulutustyyppi
            CachedPeruste cperuste = ops.getPeruste();
            String koulutustyppi;
            try {
                JsonNode node = objMapper.readTree(cperuste.getPeruste());
                JsonNode koulutustyyppi = node.get("koulutustyyppi");
                koulutustyppi = koulutustyyppi.asText();
            } catch (IOException ex) {
                throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
            }

            // Luodaan tutkinnon osat tekstikappale
            Map<Kieli, String> tutkinnonOsatTekstikappale = new HashMap<>();
            for (Kieli kieli : Kieli.values()) {
                if (KoulutusTyyppi.of(koulutustyppi).isValmaTelma()) {
                    tutkinnonOsatTekstikappale.put(kieli, messages.translate("koulutuksen-osat", kieli));
                } else {
                    tutkinnonOsatTekstikappale.put(kieli, messages.translate("tutkinnon-osat", kieli));
                }
            }
            tk.setNimi(LokalisoituTeksti.of(tutkinnonOsatTekstikappale));

            tk.setValmis(true);
            tosat.setTekstiKappale(tkRepository.save(tk));
            tosat.setLiikkumaton(false);
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
            Map<Kieli, String> suorituspolutTekstikappale = new HashMap<>();
            for (Kieli kieli : Kieli.values()) {
                suorituspolutTekstikappale.put(kieli, messages.translate("suorituspolut", kieli));
            }
            tk.setNimi(LokalisoituTeksti.of(suorituspolutTekstikappale));
            tk.setValmis(true);
            suorituspolut.setTekstiKappale(tkRepository.save(tk));
            suorituspolut.setPakollinen(true);
            suorituspolut.setTyyppi(SisaltoTyyppi.SUORITUSPOLUT);
            suorituspolut.setOwner(ops);
            suorituspolut.setLiikkumaton(false);
            suorituspolut.setVanhempi(rootTkv);
            rootTkv.getLapset().add(tkvRepository.save(suorituspolut));
        }
    }

    private Opetussuunnitelma findOps(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);

        // TODO: Tarkista kaveriorganisaatio
        if (!ops.getKoulutustoimija().getId().equals(kt.getId())) {
            throw new BusinessRuleViolationException("ops-ei-koulutustoimijan");
        }
        return ops;
    }

    private void setOpsCommon(Opetussuunnitelma ops, PerusteDto peruste, SisaltoViite rootTkv) {
        if (peruste == null) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }

        CachedPeruste cperuste;
        if (ops.getTyyppi() == OpsTyyppi.YLEINEN) {
            // EP-1392
            // Yhteinen pohja
            cperuste = cachedPerusteRepository.findOneByPerusteIdAndLuotu(peruste.getId(), peruste.getGlobalVersion().getAikaleima());
        } else {
            cperuste = cachedPerusteRepository.findOneByDiaarinumeroAndLuotu(peruste.getDiaarinumero(), peruste.getGlobalVersion().getAikaleima());
        }

        if (cperuste == null) {
            cperuste = new CachedPeruste();
            if (peruste.getNimi() != null) {
                cperuste.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
            }

            cperuste.setDiaarinumero(peruste.getDiaarinumero());
            cperuste.setPerusteId(peruste.getId());
            cperuste.setLuotu(peruste.getGlobalVersion().getAikaleima());
            cperuste.setPeruste(ops.getTyyppi() == OpsTyyppi.YLEINEN
                    ? eperusteetClient.getYleinenPohjaSisalto()
                    : eperusteetClient.getPerusteData(peruste.getId()));
            cperuste.setKoulutustyyppi(peruste.getKoulutustyyppi());
            cperuste.setKoulutukset(peruste.getKoulutukset());
            cperuste = cachedPerusteRepository.save(cperuste);
        }

        ops.setPeruste(cperuste);
        alustaOpetussuunnitelma(ops, rootTkv);

        PerusteKaikkiDto perusteSisalto = eperusteetService.getPerusteSisalto(cperuste, PerusteKaikkiDto.class);

        if (perusteSisalto.getTutkinnonOsat() == null) {
            perusteSisalto.setTutkinnonOsat(new ArrayList<>());
        }

        Map<Long, TutkinnonosaKaikkiDto> idToTosaMap = perusteSisalto.getTutkinnonOsat().stream()
                .collect(Collectors.toMap(TutkinnonosaKaikkiDto::getId, Function.identity()));

        List<TutkinnonosaKaikkiDto> tutkinnonOsat;

        if (ops.getTyyppi() == OpsTyyppi.YLEINEN) {
            tutkinnonOsat = perusteSisalto.getTutkinnonOsat();
        } else {
            tutkinnonOsat = perusteSisalto.getSuoritustavat().stream()
                    .filter(st -> st.getSuoritustapakoodi() == Suoritustapakoodi.of(ops.getSuoritustapa()))
                    .map(st -> st.getTutkinnonOsat().stream())
                    .reduce(Stream::concat)
                    .get()
                    .sorted((a, b) -> {
                        if (a.getJarjestys() == null && b.getJarjestys() == null) {
                            return 0;
                        }
                        if (a.getJarjestys() == null) {
                            return -1;
                        }
                        if (b.getJarjestys() == null) {
                            return 1;
                        }
                        return a.getJarjestys() > b.getJarjestys()
                                ? 1
                                : -1;
                    })
                    .map(tosa -> idToTosaMap.get(tosa.getTutkinnonOsa()))
                    .collect(Collectors.toList());
        }

        SisaltoViite tosat = rootTkv.getLapset().get(0);
        for (TutkinnonosaKaikkiDto tosa : tutkinnonOsat) {
            SisaltoViite uusi = SisaltoViite.createTutkinnonOsa(tosat);
            uusi.setPakollinen(false);
            uusi.getTekstiKappale().setNimi(LokalisoituTeksti.of(tosa.getNimi()));
            Tutkinnonosa uusiTosa = uusi.getTosa();
            uusiTosa.setTyyppi(TutkinnonosaTyyppi.PERUSTEESTA);
            uusiTosa.setPerusteentutkinnonosa(tosa.getId());
            uusiTosa.setKoodi(tosa.getKoodiUri());
            tkvRepository.save(uusi);
        }
    }

    @Override
    public List<VanhentunutPohjaperusteDto> haePaivitystaVaativatPerusteet(Long ktId) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        List<VanhentunutPohjaperusteDto> result = new ArrayList<>();
        List<Opetussuunnitelma> opsit = repository.findAllByKoulutustoimijaAndTyyppi(kt, OpsTyyppi.OPS);
        for (Opetussuunnitelma ops : opsit) {
            PerusteDto perusteDto = eperusteetClient.getPerusteOrNull(ops.getPeruste().getPerusteId(), PerusteDto.class);
            if (perusteDto == null || ops.getTila() != Tila.LUONNOS) {
                continue;
            }
            CachedPeruste cperuste = ops.getPeruste();
            if (perusteDto.getGlobalVersion().getAikaleima().getTime() > cperuste.getLuotu().getTime()) {
                VanhentunutPohjaperusteDto vpp = new VanhentunutPohjaperusteDto();
                PerusteDto perusteVanha = eperusteetService.getPerusteSisalto(cperuste.getId(), PerusteDto.class);
                vpp.setOpetussuunnitelma(mapper.map(ops, OpetussuunnitelmaBaseDto.class));
                vpp.setPerusteVanha(perusteVanha);
                vpp.setPerusteUusi(perusteDto);
                result.add(vpp);
            }
        }
        return result;
    }

    @Override
    public void paivitaPeruste(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.getOne(opsId);
        PerusteDto perusteDto = eperusteetClient.getPeruste(ops.getPeruste().getPerusteId(), PerusteDto.class);
        CachedPerusteBaseDto cp = eperusteetService.getCachedPeruste(perusteDto);
        CachedPeruste newCachedPeruste = cachedPerusteRepository.findOne(cp.getId());
        JsonNode st = eperusteetService.getSuoritustapa(newCachedPeruste.getId(), ops.getSuoritustapa());
        Set<UUID> tunnisteet = eperusteetService.getRakenneTunnisteet(newCachedPeruste.getId(), ops.getSuoritustapa());
        Set<UUID> kaytetytTunnisteet = tkvService.getSuorituspolkurakenne(ktId, opsId).stream()
                .map(AbstractRakenneOsaDto::getTunniste)
                .collect(Collectors.toSet());

        if (!tunnisteet.containsAll(kaytetytTunnisteet)) {
            log.error("Opetussuunnitelman perusteen synkronointi epäonnistui",
                    "\n  ops:", ops.getId(),
                    "\n  peruste:", perusteDto.getId(),
                    "\n  old:", ops.getPeruste().getLuotu(),
                    "\n  new:", newCachedPeruste.getLuotu());
            throw new BusinessRuleViolationException("ei-voi-synkronoida");
        }

        ops.setPeruste(newCachedPeruste);
    }

    @Override
    @Transactional
    public OpetussuunnitelmaBaseDto addOpetussuunnitelma(Long ktId, OpetussuunnitelmaLuontiDto opsDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops;

        if (opsDto.getOpsId() != null) {
            Opetussuunnitelma pohja = repository.findOne(opsDto.getOpsId());

            if (pohja == null || !Objects.equals(pohja.getKoulutustoimija().getId(), ktId)) {
                throw new BusinessRuleViolationException("ei-oikeutta-opetussuunnitelmaan");
            }

            SisaltoViite sisaltoRoot = tkvRepository.findOneRoot(pohja);
            ops = pohja.copy();
            if (opsDto.getNimi() != null) {
                ops.setNimi(mapper.map(opsDto.getNimi(), LokalisoituTeksti.class));
            }
            ops = repository.save(ops);
            tkvRepository.save(tkvService.kopioiHierarkia(sisaltoRoot, ops));
        }
        else {
            ops = mapper.map(opsDto, Opetussuunnitelma.class);
            ops.changeKoulutustoimija(kt);
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
                opsDto.setSuoritustapa("pohja");
            }
            else {
                switch (opsDto.getTyyppi()) {
                    case OPS:
                        PerusteDto peruste = eperusteetClient.getPeruste(opsDto.getPerusteId(), PerusteDto.class);
                        setOpsCommon(ops, peruste, rootTkv);
                        break;
                    case YLEINEN:
                        PerusteDto yleinen = eperusteetClient.getYleinenPohja();
                        setOpsCommon(ops, yleinen, rootTkv);
                        opsDto.setSuoritustapa("yleinen");
                        break;
                    case YHTEINEN:
                        Opetussuunnitelma pohja = repository.findOne(opsDto.getPohja().getIdLong());
                        if (pohja == null) {
                            throw new BusinessRuleViolationException("pohjaa-ei-loytynyt");
                        } else if (pohja.getTila() != Tila.JULKAISTU) {
                            throw new BusinessRuleViolationException("vain-julkaistua-pohjaa-voi-kayttaa");
                        }

                        opsDto.setSuoritustapa("yhteinen");
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
        }

        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaId(Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loydy");
        }
        return mapper.map(ops.getKoulutustoimija(), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public OpetussuunnitelmaDto getOpetussuunnitelma(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaKaikki(Long ktId, Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        SisaltoViiteExportDto sisalto = tkvService.getSisaltoRoot(ktId, opsId, SisaltoViiteExportDto.class);
        List<SuorituspolkuRakenneDto> suorituspolut = tkvService.getSuorituspolkurakenne(ktId, opsId);
        List<TutkinnonosaExportDto> tutkinnonOsat = tkvService.getTutkinnonOsaViitteet(ktId, opsId, TutkinnonosaExportDto.class);
        OpetussuunnitelmaKaikkiDto result = mapper.map(ops, OpetussuunnitelmaKaikkiDto.class);
        result.setSisalto(sisalto);
        result.setSuorituspolut(suorituspolut);
        result.setTutkinnonOsat(tutkinnonOsat);
        return result;
    }

    @Override
    public List<PoistettuDto> getPoistetut(Long ktId, Long id) {
        return new ArrayList<>();
    }

    @Override
    public OpetussuunnitelmaDto update(Long ktId, Long opsId, OpetussuunnitelmaDto body) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        body.setId(opsId);
        body.setTila(ops.getTila());
        repository.setRevisioKommentti(body.getKommentti());
        String paatosnumero = body.getPaatosnumero();
        if (!ObjectUtils.isEmpty(paatosnumero)
                && repository.countByPaatosnumeroAndIdNot(paatosnumero, opsId) > 0) {
            throw new BusinessRuleViolationException("paatosnumero-on-jo-kaytossa");
        }
        Opetussuunnitelma updated = mapper.map(body, ops);
        return mapper.map(updated, OpetussuunnitelmaDto.class);
    }

    @Override
    public KayttajaoikeusDto updateOikeus(Long ktId, Long opsId, Long kayttajaId, KayttajaoikeusDto oikeusDto) {
        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);

        Kayttajaoikeus oikeus = kayttajaoikeusRepository.findOneByKayttajaAndOpetussuunnitelma(kayttaja, ops);

        if (oikeusDto.getOikeus() == KayttajaoikeusTyyppi.ESTETTY) {
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
        return mapper.mapAsList(
                kayttajaoikeusRepository.findAllByKoulutustoimijaAndOpetussuunnitelma(kt, ops),
                KayttajaoikeusDto.class
        );
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
    public OpetussuunnitelmaBaseDto updateTila(Long ktId, Long opsId, Tila tila, boolean generatePDF) {
        Opetussuunnitelma ops = findOps(ktId, opsId);
        Tila nykyinen = ops.getTila();
        if (nykyinen.mahdollisetSiirtymat().contains(tila)) {

            // Julkaisun rutiinit
            if (tila.equals(Tila.JULKAISTU)) {

                validoi(ktId, opsId).tuomitse();

                // Testeissä pois käytöstä
                if (generatePDF) {
                    for (Kieli kieli : ops.getJulkaisukielet()) {
                        try {
                            DokumenttiDto dokumenttiDto = dokumenttiService.getDto(ktId, opsId, kieli);
                            dokumenttiService.setStarted(ktId, opsId, dokumenttiDto);
                            dokumenttiService.generateWithDto(ktId, opsId, dokumenttiDto);
                        } catch (DokumenttiException e) {
                            LOG.error(e.getLocalizedMessage(), e.getCause());
                        }
                    }
                }

            }

            // Muutetaan tila
            ops.setTila(tila);
        } else {
            throw new IllegalArgumentException(tila + " ei ole kelvollinen tilasiirtymä");
        }

        return mapper.map(ops, OpetussuunnitelmaBaseDto.class);
    }

    @Override
    public Validointi validoi(Long ktId, Long opsId) {
        Opetussuunnitelma ops = findOps(ktId, opsId);
        return validointiService.validoi(ops);
    }

    @Override
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmat(PageRequest p, OpetussuunnitelmaQueryDto pquery) {
        return repository.findBy(p, pquery)
                .map(ops -> mapper.map(ops, OpetussuunnitelmaDto.class));
    }

    @Override
    public OpetussuunnitelmaDto updateKoulutustoimija(Long ktId, Long opsId, KoulutustoimijaBaseDto body) {
        Opetussuunnitelma ops = findOps(ktId, opsId);
        if (!ops.getKoulutustoimija().getId().equals(ktId)) {
            throw new BusinessRuleViolationException("vain-oman-opetussuunnitelman-voi-siirtaa");
        }

        if (koulutustoimijaService.getOmatYstavat(ktId).stream()
                .noneMatch(ystava -> ystava.getId().equals(body.getId()))) {
            throw new BusinessRuleViolationException("siirto-mahdollinen-vain-ystavaorganisaatiolle");
        }

        Koulutustoimija kt = koulutustoimijaRepository.findOne(body.getId());
        ops.changeKoulutustoimija(kt);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public void updateOpetussuunnitelmaSisaltoviitePiilotukset() {

        List<Opetussuunnitelma> opetussuunnitelmat = repository.findAll();

        opetussuunnitelmat.forEach(opetussuunnitelma -> {
            List<SisaltoViite> polut = sisaltoviiteRepository.findSuorituspolut(opetussuunnitelma);
            List<SisaltoViiteDto> sisaltoviitteet = mapper.mapAsList(polut, SisaltoViiteDto.class);
            log.info("Päivitetään tutkintonimikkeet ja osaamisalat: {}", opetussuunnitelma.getId());
            sisaltoviitteet.forEach(sisaltoviite -> {
                try {
                    tkvService.updateOpetussuunnitelmaPiilotetutSisaltoviitteet(sisaltoviite, opetussuunnitelma);
                }
                catch (BusinessRuleViolationException ex) {
                    log.error(ex.getLocalizedMessage());
                }
            });
        });
    }

    @Override
    public OpetussuunnitelmaDto updateKoulutustoimijaPassivoidusta(Long ktId, Long opsId) {

        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = repository.findOne(opsId);
        List<OrganisaatioHistoriaLiitosDto> historiaLiitosOrganisaatiot = organisaatioService
                .getOrganisaationHistoriaLiitokset(kt.getOrganisaatio());

        if (historiaLiitosOrganisaatiot.stream()
                .filter(historialiitos -> historialiitos.getOrganisaatio().getStatus().equals(OrganisaatioStatus.PASSIIVINEN.name())
                        && historialiitos.getOrganisaatio().getOid().equals(ops.getKoulutustoimija().getOrganisaatio()))
                .collect(Collectors.toList()).isEmpty()) {
            throw new BusinessRuleViolationException("siirto-mahdollinen-aiemmin-passivoidulta-organisaatiolta");
        }

        ops.changeKoulutustoimija(kt);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }
}

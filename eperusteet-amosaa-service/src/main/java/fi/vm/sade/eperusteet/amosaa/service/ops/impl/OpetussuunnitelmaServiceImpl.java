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
package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.Tyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.*;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.external.KoodistoService;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.amosaa.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.amosaa.service.teksti.KommenttiService;
import fi.vm.sade.eperusteet.amosaa.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.amosaa.service.util.Validointi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fi.vm.sade.eperusteet.amosaa.service.util.Nulls.assertExists;
import static java.util.Optional.ofNullable;

/**
 *
 * @author mikkom
 */
@Service
@Transactional
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {
    static private final Logger logger = LoggerFactory.getLogger(OpetussuunnitelmaServiceImpl.class);

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private TekstikappaleviiteRepository viiteRepository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KommenttiService kommenttiService;

    @Autowired
    private EperusteetService eperusteetService;


    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaJulkinenDto getOpetussuunnitelmaJulkinen(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaJulkinenDto dto = mapper.map(ops, OpetussuunnitelmaJulkinenDto.class);
        //fetchKuntaNimet(dto);
        //fetchOrganisaatioNimet(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getAll() {
//        Set<String> organisaatiot = SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class));
//        final List<Opetussuunnitelma> opetussuunnitelmat;
//        if (tyyppi == Tyyppi.POHJA) {
//            opetussuunnitelmat = repository.findPohja(organisaatiot);
//        } else {
//            opetussuunnitelmat = repository.findAllByTyyppi(tyyppi, organisaatiot);
//        }
//        final List<OpetussuunnitelmaInfoDto> dtot = mapper.mapAsList(opetussuunnitelmat,
//                OpetussuunnitelmaInfoDto.class);
//        dtot.forEach(dto -> {
//            fetchKuntaNimet(dto);
//            fetchOrganisaatioNimet(dto);
//        });
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaStatistiikkaDto> getStatistiikka() {
        List<Opetussuunnitelma> opsit = repository.findAllByTyyppi(Tyyppi.OPS);
        return mapper.mapAsList(opsit, OpetussuunnitelmaStatistiikkaDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PerusteDto getPeruste(Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaKevytDto getOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaKevytDto dto = mapper.map(ops, OpetussuunnitelmaKevytDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);
        return dto;
    }

    private void fetchLapsiOpetussuunnitelmat(Long id, Set<Opetussuunnitelma> opsit) {
        opsit.addAll(repository.findAllByPohjaId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getLapsiOpetussuunnitelmat(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        Set<Opetussuunnitelma> result = new HashSet<>();
        fetchLapsiOpetussuunnitelmat(id, result);
        return mapper.mapAsList(result, OpetussuunnitelmaInfoDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaDto getOpetussuunnitelmaKaikki(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaDto dto = mapper.map(ops, OpetussuunnitelmaDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);
        return dto;
    }

    private void fetchKuntaNimet(OpetussuunnitelmaBaseDto opetussuunnitelmaDto) {
        for (KoodistoDto koodistoDto : opetussuunnitelmaDto.getKunnat()) {
            Map<String, String> tekstit = new HashMap<>();
            KoodistoKoodiDto kunta = koodistoService.get("kunta", koodistoDto.getKoodiUri());
            if (kunta != null) {
                for (KoodistoMetadataDto metadata : kunta.getMetadata()) {
                    tekstit.put(metadata.getKieli(), metadata.getNimi());
                }
            }
            koodistoDto.setNimi(new LokalisoituTekstiDto(tekstit));
        }
    }

    private void fetchOrganisaatioNimet(OpetussuunnitelmaBaseDto opetussuunnitelmaDto) {
        for (OrganisaatioDto organisaatioDto : opetussuunnitelmaDto.getOrganisaatiot()) {
            Map<String, String> tekstit = new HashMap<>();
            List<String> tyypit = new ArrayList<>();
            JsonNode organisaatio = organisaatioService.getOrganisaatio(organisaatioDto.getOid());
            if (organisaatio != null) {
                JsonNode nimiNode = organisaatio.get("nimi");
                if (nimiNode != null) {
                    Iterator<Map.Entry<String, JsonNode>> it = nimiNode.fields();
                    while (it.hasNext()) {
                        Map.Entry<String, JsonNode> field = it.next();
                        tekstit.put(field.getKey(), field.getValue().asText());
                    }
                }

                JsonNode tyypitNode = ofNullable(organisaatio.get("tyypit"))
                        .orElse(organisaatio.get("organisaatiotyypit"));
                if (tyypitNode != null) {
                    tyypit = StreamSupport.stream(tyypitNode.spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.toList());
                }
            }
            organisaatioDto.setNimi(new LokalisoituTekstiDto(tekstit));
            organisaatioDto.setTyypit(tyypit);
        }
    }

    @Override
    public OpetussuunnitelmaDto addOpetussuunnitelma(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto) {
        opetussuunnitelmaDto.setTyyppi(Tyyppi.OPS);
        Opetussuunnitelma ops = mapper.map(opetussuunnitelmaDto, Opetussuunnitelma.class);

        Set<String> userOids = SecurityUtil.getOrganizations(EnumSet.of(RolePermission.CRUD,
                RolePermission.ADMIN));
        if (CollectionUtil.intersect(userOids, ops.getOrganisaatiot()).isEmpty()) {
            throw new BusinessRuleViolationException("Käyttäjällä ei ole luontioikeutta "
                    + "opetussuunnitelman organisaatioissa");
        }

        Opetussuunnitelma pohja = ops.getPohja();

        if (pohja == null) {
            pohja = repository.findOneByTyyppiAndTilaAndKoulutustyyppi(Tyyppi.POHJA,
                    Tila.VALMIS, opetussuunnitelmaDto.getKoulutustyyppi());
        }

        if (pohja != null) {
            ops.setTekstit(new TekstiKappaleViite(Omistussuhde.OMA));
            ops.getTekstit().setLapset(new ArrayList<>());
            ops.setTila(Tila.LUONNOS);
            ops = repository.save(ops);
        } else {
            throw new BusinessRuleViolationException("Valmista opetussuunnitelman pohjaa ei löytynyt");
        }

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    private void kasitteleTekstit(TekstiKappaleViite vanha, TekstiKappaleViite parent, boolean teeKopio) {
        List<TekstiKappaleViite> vanhaLapset = vanha.getLapset();
        if (vanhaLapset != null) {
            vanhaLapset.stream()
                    .filter(vanhaTkv -> vanhaTkv.getTekstiKappale() != null)
                    .forEach(vanhaTkv -> {
                        TekstiKappaleViite tkv = viiteRepository.save(new TekstiKappaleViite());
                        tkv.setOmistussuhde(teeKopio ? Omistussuhde.OMA : Omistussuhde.LAINATTU);
                        tkv.setLapset(new ArrayList<>());
                        tkv.setVanhempi(parent);
                        tkv.setPakollinen(vanhaTkv.isPakollinen());
                        tkv.setTekstiKappale(teeKopio
                                ? tekstiKappaleRepository.save(vanhaTkv.getTekstiKappale().copy())
                                : vanhaTkv.getTekstiKappale());
                        parent.getLapset().add(tkv);
                        kasitteleTekstit(vanhaTkv, tkv, teeKopio);
                    });
        }
    }

    @Override
    public void syncPohja(Long pohjaId) {
        Opetussuunnitelma pohja = repository.findOne(pohjaId);
        if (pohja.getPohja() != null) {
            throw new BusinessRuleViolationException("OPS ei ollut pohja");
        }
    }

    private Opetussuunnitelma lisaaPerusteenSisalto(Opetussuunnitelma ops, PerusteDto peruste) {
        throw new BusinessRuleViolationException("Ei toimintatapaa perusteen koulutustyypille");
    }

    private void flattenTekstikappaleviitteet(Map<UUID, TekstiKappaleViite> viitteet, TekstiKappaleViite tov) {
        if (tov.getLapset() == null) {
            return;
        }
        for (TekstiKappaleViite lapsi : tov.getLapset()) {
            // Tätä tarkistusta ei välttämättä tarvitse
            if (viitteet.get(lapsi.getTekstiKappale().getTunniste()) != null) {
                continue;
            }
            viitteet.put(lapsi.getTekstiKappale().getTunniste(), lapsi);
            flattenTekstikappaleviitteet(viitteet, lapsi);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateLapsiOpetussuunnitelmat(Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Päivitettävää tietoa ei ole olemassa");
        Set<Opetussuunnitelma> aliopsit = repository.findAllByPohjaId(opsId);

        for (Opetussuunnitelma aliops : aliopsit) {
            Map<UUID, TekstiKappaleViite> aliopsTekstit = new HashMap<>();
            flattenTekstikappaleviitteet(aliopsTekstit, aliops.getTekstit());
            aliops.getTekstit().getLapset().clear();
            aliopsTekstit.values().stream()
                    .forEach((teksti) -> {
                        teksti.setVanhempi(aliops.getTekstit());
                        teksti.getLapset().clear();
                    });
//            aliops.getTekstit().getLapset().addAll(aliopsTekstit.values());
//            for (TekstiKappaleViite lapsi : ops.getTekstit().getLapset()) {
//                TekstiKappaleViite uusiSolmu = viiteRepository.save(lapsi.kopioiHierarkia(aliopsTekstit));
//                uusiSolmu.setVanhempi(aliops.getTekstit());
//                aliops.getTekstit().getLapset().add(uusiSolmu);
//            }
//            aliops.getTekstit().getLapset().addAll(aliopsTekstit.values());
        }
    }

    @Override
    public OpetussuunnitelmaDto updateOpetussuunnitelma(OpetussuunnitelmaDto opetussuunnitelmaDto) {
        Opetussuunnitelma ops = repository.findOne(opetussuunnitelmaDto.getId());

        // Tilan muuttamiseen on oma erillinen endpointtinsa
        opetussuunnitelmaDto.setTila(ops.getTila());

        mapper.map(opetussuunnitelmaDto, ops);
        ops = repository.save(ops);

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    private void validoiOpetussuunnitelma(Opetussuunnitelma ops) {
        Set<Kieli> julkaisukielet = ops.getJulkaisukielet();
        Validointi validointi = new Validointi();

        if (ops.getPerusteenDiaarinumero().isEmpty()) {
            validointi.lisaaVirhe(Validointi.luoVirhe("opsilla-ei-perusteen-diaarinumeroa"));
        }

        if (ops.getTekstit() != null && ops.getTekstit().getLapset() != null) {
            for (TekstiKappaleViite teksti : ops.getTekstit().getLapset()) {
                TekstiKappaleViite.validoi(validointi, teksti, julkaisukielet);
            }
        }

        validointi.tuomitse();
    }

    private void validoiPohja(Opetussuunnitelma ops) {
    }

    @Override
    public OpetussuunnitelmaDto updateTila(@P("id") Long id, Tila tila) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public OpetussuunnitelmaDto restore(@P("id") Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");

        ops.setTila(Tila.LUONNOS);
        ops = repository.save(ops);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public void removeOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        if (ops != null) {
            kommenttiService.getAllByOpetussuunnitelma(id)
                    .forEach(k -> kommenttiService.deleteReally(k.getId()));
        }
        repository.delete(ops);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T getTekstit(Long opsId, Class<T> t) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        return mapper.map(ops.getTekstit(), t);
    }

    @Override
    public TekstiKappaleViiteDto.Matala addTekstiKappale(Long opsId, TekstiKappaleViiteDto.Matala viite) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        // Lisätään viite juurinoden alle
        return tekstiKappaleViiteService.addTekstiKappaleViite(opsId, ops.getTekstit().getId(), viite);
    }

    @Override
    public TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(Long opsId, Long parentId,
            TekstiKappaleViiteDto.Matala viite) {
        // Lisätään viite parent-noden alle
        return tekstiKappaleViiteService.addTekstiKappaleViite(opsId, parentId, viite);
    }

}

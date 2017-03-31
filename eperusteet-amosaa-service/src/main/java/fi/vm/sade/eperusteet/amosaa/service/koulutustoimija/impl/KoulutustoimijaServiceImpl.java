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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti_;
import fi.vm.sade.eperusteet.amosaa.dto.PageDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.YstavaStatus;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.KoulutustoimijaService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author nkala
 */
@Service
@Transactional
public class KoulutustoimijaServiceImpl implements KoulutustoimijaService {
    private static final Logger LOG = LoggerFactory.getLogger(KoulutustoimijaServiceImpl.class);

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutustoimijaRepository repository;

    @Autowired
    private SisaltoviiteRepository sisaltoviiteRepository;

    @Autowired
    private DtoMapper mapper;

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = false)
    private Koulutustoimija initialize(String kOid) {
        Koulutustoimija koulutustoimija = repository.findOneByOrganisaatio(kOid);
        if (koulutustoimija != null) {
            return koulutustoimija;
        }

        LOG.info("Luodaan uusi organisaatiota vastaava koulutustoimija ensimmäistä kertaa", kOid);
        JsonNode organisaatio = organisaatioService.getOrganisaatio(kOid);
        if (organisaatio == null) {
            return null;
        }

        LokalisoituTeksti nimi = LokalisoituTeksti.of(Kieli.FI, organisaatio.get("nimi").get("fi").asText());
        koulutustoimija = new Koulutustoimija();
        koulutustoimija.setNimi(nimi);
        koulutustoimija.setOrganisaatio(kOid);
        koulutustoimija = repository.save(koulutustoimija);
        return koulutustoimija;
    }

    @Override
    public List<KoulutustoimijaBaseDto> initKoulutustoimijat(Set<String> kOid) {
        return kOid.stream()
                .map(this::initialize)
                .filter(kt -> kt != null)
                .map(kt -> mapper.map(kt, KoulutustoimijaBaseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public KoulutustoimijaDto updateKoulutustoimija(Long ktId, KoulutustoimijaDto ktDto) {
        Koulutustoimija toimija = repository.findOne(ktId);
        if (ktDto.getYstavat() == null) {
            ktDto.setYstavat(new HashSet<>());
        }
        Koulutustoimija uusi = mapper.map(ktDto, Koulutustoimija.class);
        uusi.getYstavat().remove(toimija);
        toimija.setKuvaus(uusi.getKuvaus());
        toimija.setYstavat(uusi.getYstavat());
        toimija.getYstavat();
        toimija.setSalliystavat(uusi.isSalliystavat());
        return mapper.map(toimija, KoulutustoimijaDto.class);
    }

    @Override
    @Transactional(readOnly = false)
    public List<KoulutustoimijaBaseDto> getKoulutustoimijat(Set<String> kOid) {
        return kOid.stream()
            .map(ktId -> {
                LOG.info("Käyttäjän koulutustoimija", ktId);
                Koulutustoimija kt = repository.findOneByOrganisaatio(ktId);
                return mapper.map(kt, KoulutustoimijaBaseDto.class);
            })
            .collect(Collectors.toList());
    }

    @Override
    public Page<KoulutustoimijaJulkinenDto> findKoulutustoimijat(PageRequest page, KoulutustoimijaQueryDto pquery) {
        Page<Koulutustoimija> toimijat = findBy(page, pquery);
        PageDto<Koulutustoimija, KoulutustoimijaJulkinenDto> resultDto = new PageDto<>(toimijat, KoulutustoimijaJulkinenDto.class, page, mapper);
        return null;
    }

    private Page<Koulutustoimija> findBy(PageRequest page, KoulutustoimijaQueryDto pquery) {
        TypedQuery<Long> countQuery = getCountQuery(pquery);
        TypedQuery<Tuple> query = getQuery(pquery);
        if (page != null) {
            query.setFirstResult(page.getOffset());
            query.setMaxResults(page.getPageSize());
        }
        return new PageImpl<>(Lists.transform(query.getResultList(), (item) -> item.get(0, Koulutustoimija.class)), page, countQuery.getSingleResult());
    }

    private TypedQuery<Tuple> getQuery(KoulutustoimijaQueryDto pquery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Koulutustoimija> root = query.from(Koulutustoimija.class);
        SetJoin<LokalisoituTeksti, Teksti> teksti = root.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
        Predicate pred = buildPredicate(root, teksti, cb, pquery);
        query.distinct(true);
        final Expression<String> n = cb.lower(teksti.get(Teksti_.teksti));

        final List<Order> order = new ArrayList<>();
        if ("muokattu".equals(pquery.getJarjestys())) {
            order.add(cb.desc(root.get(Koulutustoimija_.luotu)));
        }
        order.add(cb.asc(n));
        order.add(cb.asc(root.get(Koulutustoimija_.id)));
//        query.multiselect(root, n).where(pred).orderBy(order);

        return em.createQuery(query);
    }

    private TypedQuery<Long> getCountQuery(KoulutustoimijaQueryDto pquery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Koulutustoimija> root = query.from(Koulutustoimija.class);
        Join<LokalisoituTeksti, Teksti> teksti = root.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
        Predicate pred = buildPredicate(root, teksti, cb, pquery);
        query.select(cb.countDistinct(root)).where(pred);
        return em.createQuery(query);
    }

    private Predicate buildPredicate(
        Root<Koulutustoimija> root,
        Join<LokalisoituTeksti, Teksti> teksti,
        CriteriaBuilder cb,
        KoulutustoimijaQueryDto pq
    ) {
        final Kieli kieli = Kieli.of(pq.getKieli());
//        Predicate pred = cb.equal(teksti.get(LokalisoituTeksti_.kieli), kieli);

        if (pq.getNimi() != null) {
//            Predicate nimessa = cb.like(cb.lower(teksti.get(LokalisoituTeksti_.teksti)), cb.literal(RepositoryUtil.kuten(pq.getNimi())));
//            pred = cb.and(pred, nimessa);
        }

//        if (pq.getDiaarinumero() != null) {
//            pred = cb.and(pred, cb.equal(root.get(Koulutustoimija_.diaarinumero), cb.literal(new Diaarinumero(pq.getDiaarinumero()))));
//        }

//        if (!empty(pq.getKoulutustyyppi())) {
//            pred = cb.and(pred, root.get(Koulutustoimija_.koulutustyyppi).in(pq.getKoulutustyyppi()));
//        }

        return null;
//        return pred;
    }

    @Override
    public KoulutustoimijaDto getKoulutustoimija(Long kId) {
        return mapper.map(repository.findOne(kId), KoulutustoimijaDto.class);
    }

    @Override
    public Long getKoulutustoimija(String idTaiOid) {
        Long result;
        try {
            result = repository.findOne(Long.parseLong(idTaiOid)).getId();
        }
        catch (NumberFormatException ex) {
            result = repository.findOneIdByOrganisaatio(idTaiOid);
        }
        return result;
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(Long kId) {
        return mapper.map(repository.findOne(kId), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public KoulutustoimijaJulkinenDto getKoulutustoimijaJulkinen(String ktOid) {
        return mapper.map(repository.findOneByOrganisaatio(ktOid), KoulutustoimijaJulkinenDto.class);
    }

    @Override
    public <T> List<T> getPaikallisetTutkinnonOsat(Long ktId, Class<T> tyyppi) {
        Koulutustoimija kt = repository.findOne(ktId);
        return mapper.mapAsList(sisaltoviiteRepository.findAllPaikallisetTutkinnonOsat(kt), tyyppi);
    }

    @Override
    public List<KoulutustoimijaYstavaDto> getOmatYstavat(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        List<KoulutustoimijaYstavaDto> result = kt.getYstavat().stream()
            .map(ystava -> {
                KoulutustoimijaYstavaDto ystavaDto = mapper.map(ystava, KoulutustoimijaYstavaDto.class);
                ystavaDto.setStatus(ystava.getYstavat() != null && ystava.getYstavat().contains(kt)
                        ? YstavaStatus.YHTEISTYO
                        : YstavaStatus.ODOTETAAN);
                return ystavaDto;
            })
            .collect(Collectors.toList());

        return result;
    }

    @Override
    public List<KoulutustoimijaBaseDto> getYhteistyoKoulutustoimijat(Long ktId) {
        return mapper.mapAsList(repository.findAllYstavalliset(), KoulutustoimijaBaseDto.class);
    }

    @Override
    public List<KoulutustoimijaBaseDto> getPyynnot(Long ktId) {
        Koulutustoimija kt = repository.findOne(ktId);
        Set<Koulutustoimija> pyynnot = repository.findAllYstavaPyynnotForKoulutustoimija(kt);
        pyynnot.removeAll(kt.getYstavat());
        return mapper.mapAsList(pyynnot, KoulutustoimijaBaseDto.class);
    }

}

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

package fi.vm.sade.eperusteet.amosaa.repository.custom;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti_;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaCustomRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * @author nkala
 */
public class KoulutustoimijaRepositoryImpl implements KoulutustoimijaCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Koulutustoimija> findBy(PageRequest page, KoulutustoimijaQueryDto pquery) {
        TypedQuery<Long> countQuery = getCountQuery(pquery);
        TypedQuery<Tuple> query = getQuery(pquery);
        if (page != null) {
            query.setFirstResult(page.getOffset());
            query.setMaxResults(page.getPageSize());
        }
        List<Koulutustoimija> list = query.getResultList().stream()
                .map(t -> t.get(0, Koulutustoimija.class)).collect(Collectors.toList());
        return new PageImpl<>(list,
                page,
                countQuery.getSingleResult());
    }

    private TypedQuery<Long> getCountQuery(KoulutustoimijaQueryDto pquery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        Predicate pred = buildPredicate(root, koulutustoimija, cb, pquery);
        query.select(cb.countDistinct(root.get(Opetussuunnitelma_.koulutustoimija))).where(pred);
        return em.createQuery(query);
    }

    private TypedQuery<Tuple> getQuery(KoulutustoimijaQueryDto pquery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        SetJoin<LokalisoituTeksti, Teksti> ktNimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);

        Predicate pred = buildPredicate(root, koulutustoimija, cb, pquery);
        query.distinct(true);
        final Expression<String> n = cb.lower(ktNimi.get(Teksti_.teksti));

        List<Order> orders = new ArrayList<>();
//        orders.add(cb.asc(n));
//        orders.add(cb.asc(koulutustoimija.get(Koulutustoimija_.id)));
        query.multiselect(root.get(Opetussuunnitelma_.koulutustoimija)).where(pred); //.orderBy(orders);
        return em.createQuery(query);
    }

    private Predicate buildPredicate(Root<Opetussuunnitelma> root, Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija, CriteriaBuilder cb, KoulutustoimijaQueryDto pquery) {
        Predicate pred = cb.notEqual(koulutustoimija.get(Koulutustoimija_.organisaatio), SecurityUtil.OPH_OID);
        pred = cb.and(pred, cb.equal(root.get(Opetussuunnitelma_.tila), Tila.JULKAISTU));

        if (pquery.getNimi() != null && !"".equals(pquery.getNimi())) {
            SetJoin<LokalisoituTeksti, Teksti> nimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
            Predicate nimessa = cb.like(cb.lower(nimi.get(Teksti_.teksti)), cb.literal(RepositoryUtil.kuten(pquery.getNimi())));
            pred = cb.and(pred, nimessa);
        }

        return pred;
    }

}

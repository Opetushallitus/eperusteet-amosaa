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

package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.custom;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti_;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaCustomRepository;

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
public class OpetussuunnitelmaRepositoryImpl implements OpetussuunnitelmaCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Opetussuunnitelma> findBy(PageRequest page, OpetussuunnitelmaQueryDto pquery) {
        TypedQuery<Long> countQuery = getCountQuery(pquery);
        TypedQuery<Tuple> query = getQuery(pquery);
        if (page != null) {
            query.setFirstResult(page.getOffset());
            query.setMaxResults(page.getPageSize());
        }
        return new PageImpl<>(query.getResultList().stream()
                .map(t -> t.get(0, Opetussuunnitelma.class)).collect(Collectors.toList()),
                page,
                countQuery.getSingleResult());
    }

    private TypedQuery<Long> getCountQuery(OpetussuunnitelmaQueryDto pquery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<LokalisoituTeksti, Teksti> opsNimi = root.join(Opetussuunnitelma_.nimi).join(LokalisoituTeksti_.teksti);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        Predicate pred = buildPredicate(root, opsNimi, koulutustoimija, cb, pquery);
        query.select(cb.countDistinct(root)).where(pred);
        return em.createQuery(query);
    }

    private TypedQuery<Tuple> getQuery(OpetussuunnitelmaQueryDto pquery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<LokalisoituTeksti, Teksti> opsNimi = root.join(Opetussuunnitelma_.nimi).join(LokalisoituTeksti_.teksti);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        Predicate pred = buildPredicate(root, opsNimi, koulutustoimija, cb, pquery);
        query.distinct(true);
        final Expression<String> n = cb.lower(opsNimi.get(Teksti_.teksti));

        List<Order> orders = new ArrayList<>();
        orders.add(cb.asc(n));
        orders.add(cb.asc(root.get(Opetussuunnitelma_.id)));
        query.multiselect(root, n).where(pred).orderBy(orders);
        return em.createQuery(query);
    }

    private Predicate buildPredicate(Root<Opetussuunnitelma> root, Join<LokalisoituTeksti, Teksti> opsNimi, Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija, CriteriaBuilder cb, OpetussuunnitelmaQueryDto pquery) {
        Kieli kieli = Kieli.of(pquery.getKieli());
        Predicate onOikeallaKielella = cb.equal(opsNimi.get(Teksti_.kieli), kieli);
        Predicate isJulkaistu = cb.equal(root.get(Opetussuunnitelma_.tila), Tila.JULKAISTU);
        Predicate eiOlePohja = cb.notEqual(root.get(Opetussuunnitelma_.tyyppi), OpsTyyppi.POHJA);
        Predicate pred = cb.and(isJulkaistu, onOikeallaKielella, eiOlePohja);

        if (pquery.getNimi() != null && !"".equals(pquery.getNimi())) {
            Predicate nimessa = cb.like(cb.lower(opsNimi.get(Teksti_.teksti)), cb.literal(RepositoryUtil.kuten(pquery.getNimi())));
            Predicate diaarissa = cb.like(root.get(Opetussuunnitelma_.perusteDiaarinumero), cb.literal(RepositoryUtil.kuten(pquery.getNimi())));
            SetJoin<LokalisoituTeksti, Teksti> ktNimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
            Predicate ktNimessa = cb.like(cb.lower(ktNimi.get(Teksti_.teksti)), cb.literal(RepositoryUtil.kuten(pquery.getNimi())));
            pred = cb.and(pred, cb.or(nimessa, diaarissa, ktNimessa));
        }

        if (pquery.getTyyppi() != null && !pquery.getTyyppi().isEmpty()) {
            Predicate tyyppiOn = root.get(Opetussuunnitelma_.tyyppi).in(pquery.getTyyppi());
            pred = cb.and(pred, tyyppiOn);
        }

        if (pquery.getPerusteenDiaarinumero() != null && !"".equals(pquery.getPerusteenDiaarinumero())) {
            Predicate matchDiaarinumero = cb.equal(root.get(Opetussuunnitelma_.perusteDiaarinumero), pquery.getPerusteenDiaarinumero());
            pred = cb.and(pred, matchDiaarinumero);
        }

        if (pquery.getOrganisaatio() != null && !"".equals(pquery.getOrganisaatio())) {
            Predicate matchOrganisaatio = cb.equal(koulutustoimija.get(Koulutustoimija_.organisaatio), cb.literal(pquery.getOrganisaatio()));
            pred = cb.and(pred, matchOrganisaatio);
        }

        return pred;
    }
}

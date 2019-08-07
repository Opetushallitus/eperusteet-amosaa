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
import fi.vm.sade.eperusteet.amosaa.domain.teksti.*;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaCustomRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nkala
 */
public class KoulutustoimijaRepositoryImpl implements KoulutustoimijaCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Koulutustoimija> findBy(PageRequest page, KoulutustoimijaQueryDto queryDto) {
        TypedQuery<Long> countQuery = getCountQuery(queryDto);
        TypedQuery<Tuple> query = getQuery(queryDto);
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

    private TypedQuery<Long> getCountQuery(KoulutustoimijaQueryDto queryDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        SetJoin<LokalisoituTeksti, Teksti> ktNimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
        Predicate pred = buildPredicate(root, koulutustoimija, cb, queryDto, ktNimi);
        query.select(cb.countDistinct(root.get(Opetussuunnitelma_.koulutustoimija))).where(pred);
        return em.createQuery(query);
    }

    private TypedQuery<Tuple> getQuery(KoulutustoimijaQueryDto queryDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        SetJoin<LokalisoituTeksti, Teksti> ktNimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);

        Predicate pred = buildPredicate(root, koulutustoimija, cb, queryDto, ktNimi);     
        
        query.distinct(true);
        final Expression<String> n = cb.lower(ktNimi.get(Teksti_.teksti));

        List<Order> orders = new ArrayList<>();
        orders.add(cb.asc(n));
        orders.add(cb.asc(koulutustoimija.get(Koulutustoimija_.id)));
        query.multiselect(root.get(Opetussuunnitelma_.koulutustoimija), n).where(pred).orderBy(orders);
        return em.createQuery(query);
    }

    private Predicate buildPredicate(
            Root<Opetussuunnitelma> root,
            Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija,
            CriteriaBuilder cb,
            KoulutustoimijaQueryDto queryDto,
            SetJoin<LokalisoituTeksti, Teksti> ktNimi
    ) {
        Predicate pred = cb.notEqual(koulutustoimija.get(Koulutustoimija_.organisaatio), SecurityUtil.OPH_OID);
        pred = cb.and(pred, cb.equal(root.get(Opetussuunnitelma_.tila), Tila.JULKAISTU));

        Kieli kieli = Kieli.of(queryDto.getKieli());
        if (!kieli.equals(Kieli.FI)) {
            SetJoin<Opetussuunnitelma, Kieli> kielet = root.join(Opetussuunnitelma_.julkaisukielet);
            pred = cb.and(pred, kielet.in(kieli));
        }
        
        if (!ObjectUtils.isEmpty(queryDto.getKieli())) {
	        Predicate koulutustoimijaKielella = cb.equal(ktNimi.get(Teksti_.kieli), Kieli.of(queryDto.getKieli()));
	        pred = cb.and(pred, koulutustoimijaKielella); 
        }
        
        if (!ObjectUtils.isEmpty(queryDto.getNimi())) {
            SetJoin<LokalisoituTeksti, Teksti> nimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
            Predicate nimessa = cb.like(cb.lower(nimi.get(Teksti_.teksti)), cb.literal(RepositoryUtil.kuten(queryDto.getNimi())));
            pred = cb.and(pred, nimessa);
        }

        return pred;
    }

}

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
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.*;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.*;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaCustomRepository;
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
public class OpetussuunnitelmaRepositoryImpl implements OpetussuunnitelmaCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Opetussuunnitelma> findBy(PageRequest page, OpetussuunnitelmaQueryDto queryDto) {
        TypedQuery<Long> countQuery = getCountQuery(queryDto);
        TypedQuery<Tuple> query = getQuery(queryDto);
        if (page != null) {
            query.setFirstResult(page.getOffset());
            query.setMaxResults(page.getPageSize());
        }
        return new PageImpl<>(query.getResultList().stream()
                .map(t -> t.get(0, Opetussuunnitelma.class))
                .collect(Collectors.toList()), page, countQuery.getSingleResult());
    }

    private TypedQuery<Long> getCountQuery(OpetussuunnitelmaQueryDto queryDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<LokalisoituTeksti, Teksti> opsNimi = root.join(Opetussuunnitelma_.nimi).join(LokalisoituTeksti_.teksti);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        Predicate pred = buildPredicate(root, opsNimi, koulutustoimija, cb, queryDto);
        query.select(cb.countDistinct(root)).where(pred);
        return em.createQuery(query);
    }

    private TypedQuery<Tuple> getQuery(OpetussuunnitelmaQueryDto queryDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);
        Join<LokalisoituTeksti, Teksti> opsNimi = root.join(Opetussuunnitelma_.nimi).join(LokalisoituTeksti_.teksti);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);
        Predicate pred = buildPredicate(root, opsNimi, koulutustoimija, cb, queryDto);
        query.distinct(true);
        final Expression<String> n = cb.lower(opsNimi.get(Teksti_.teksti));

        List<Order> orders = new ArrayList<>();
        orders.add(cb.asc(n));
        orders.add(cb.asc(root.get(Opetussuunnitelma_.id)));
        query.multiselect(root, n).where(pred).orderBy(orders);
        return em.createQuery(query);
    }

    private Predicate buildPredicate(
            Root<Opetussuunnitelma> root,
            Join<LokalisoituTeksti, Teksti> opsNimi,
            Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija,
            CriteriaBuilder cb,
            OpetussuunnitelmaQueryDto queryDto
    ) {
        Kieli kieli = Kieli.of(queryDto.getKieli());
        Predicate onOikeallaKielella = cb.equal(opsNimi.get(Teksti_.kieli), kieli);
        Predicate isJulkaistu = cb.equal(root.get(Opetussuunnitelma_.tila), Tila.JULKAISTU);
        Predicate eiOlePohja = cb.notEqual(root.get(Opetussuunnitelma_.tyyppi), OpsTyyppi.POHJA);
        Predicate pred = cb.and(isJulkaistu, onOikeallaKielella, eiOlePohja);

        if (queryDto.getNimi() != null && !"".equals(queryDto.getNimi())) {
            String nimi = RepositoryUtil.kuten(queryDto.getNimi());
            Predicate nimessa = cb.like(cb.lower(opsNimi.get(Teksti_.teksti)), cb.literal(nimi));
            Predicate diaarissa = cb.like(cb.lower(root.get(Opetussuunnitelma_.perusteDiaarinumero)), cb.literal(nimi));
            SetJoin<LokalisoituTeksti, Teksti> ktNimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
            Predicate ktNimessa = cb.like(cb.lower(ktNimi.get(Teksti_.teksti)), cb.literal(nimi));
            pred = cb.and(pred, cb.or(nimessa, diaarissa, ktNimessa));
        }

        if (queryDto.getTyyppi() != null && !queryDto.getTyyppi().isEmpty()) {
            Predicate tyyppiOn = root.get(Opetussuunnitelma_.tyyppi).in(queryDto.getTyyppi());
            pred = cb.and(pred, tyyppiOn);
        }

        if (queryDto.getPerusteenDiaarinumero() != null && !"".equals(queryDto.getPerusteenDiaarinumero())) {
            Predicate matchDiaarinumero = cb.equal(root.get(Opetussuunnitelma_.perusteDiaarinumero), queryDto.getPerusteenDiaarinumero());
            pred = cb.and(pred, matchDiaarinumero);
        }

        if (queryDto.getPerusteId() != null) {
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste);
            Predicate matchPeruste = cb.equal(peruste.get(CachedPeruste_.perusteId), queryDto.getPerusteId());
            pred = cb.and(pred, matchPeruste);
        }

        if (queryDto.getOrganisaatio() != null && !"".equals(queryDto.getOrganisaatio())) {
            Predicate matchOrganisaatio = cb.equal(koulutustoimija.get(Koulutustoimija_.organisaatio), cb.literal(queryDto.getOrganisaatio()));
            pred = cb.and(pred, matchOrganisaatio);
        }

        return pred;
    }

    @Override
    public Page<Opetussuunnitelma> findBy(PageRequest page, OpsHakuDto queryDto) {
        TypedQuery<Long> countQuery = getCountQuery(queryDto);
        TypedQuery<Tuple> query = getQuery(queryDto);
        if (page != null) {
            query.setFirstResult(page.getOffset());
            query.setMaxResults(page.getPageSize());
        }

        return new PageImpl<>(query.getResultList().stream()
                .map(t -> t.get(0, Opetussuunnitelma.class))
                .collect(Collectors.toList()), page, countQuery.getSingleResult());
    }

    private TypedQuery<Long> getCountQuery(OpsHakuDto queryDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);

        Join<LokalisoituTeksti, Teksti> opsNimi = root.join(Opetussuunnitelma_.nimi).join(LokalisoituTeksti_.teksti);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);

        Predicate pred = buildPredicate(root, opsNimi, koulutustoimija, cb, queryDto);
        query.select(cb.countDistinct(root)).where(pred);
        return em.createQuery(query);
    }

    private TypedQuery<Tuple> getQuery(OpsHakuDto queryDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Opetussuunnitelma> root = query.from(Opetussuunnitelma.class);

        Join<LokalisoituTeksti, Teksti> opsNimi = root.join(Opetussuunnitelma_.nimi).join(LokalisoituTeksti_.teksti);
        Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija = root.join(Opetussuunnitelma_.koulutustoimija);

        Predicate pred = buildPredicate(root, opsNimi, koulutustoimija, cb, queryDto);
        query.distinct(true);

        final Expression<String> nimi = cb.lower(opsNimi.get(Teksti_.teksti));

        List<Order> orders = new ArrayList<>();
        orders.add(cb.asc(nimi));
        orders.add(cb.asc(root.get(Opetussuunnitelma_.id)));

        query.multiselect(root, nimi).where(pred).orderBy(orders);
        return em.createQuery(query);
    }

    private Predicate buildPredicate(
            Root<Opetussuunnitelma> root,
            Join<LokalisoituTeksti, Teksti> opsNimi,
            Join<Opetussuunnitelma, Koulutustoimija> koulutustoimija,
            CriteriaBuilder cb,
            OpsHakuDto queryDto
    ) {
        if (ObjectUtils.isEmpty(queryDto.getKoulutustoimija())) {
            throw new IllegalArgumentException("Koulutustoimija puuttuu");
        }

        Predicate oikeaKoulutustoimija = cb.equal(koulutustoimija.get(Koulutustoimija_.id), queryDto.getKoulutustoimija());

        Predicate pred;
        if (!ObjectUtils.isEmpty(queryDto.getPeruste())) {
            // Rajataan koulutustoimijan ja  perusteen mukaan
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste);
            Predicate oikeaPeruste = cb.equal(peruste.get(CachedPeruste_.id), queryDto.getPeruste());
            pred = cb.and(oikeaKoulutustoimija, oikeaPeruste);
        } else if (!ObjectUtils.isEmpty(queryDto.getKoulutustyyppi())) {
            // Rajataan koulutustoimijan ja koulutustyypin mukaan
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste);
            Predicate oikeaKoulutustyyppi = cb.equal(peruste.get(CachedPeruste_.koulutustyyppi), queryDto.getKoulutustyyppi());
            Predicate koulutustyyppiTaiIlmanPerustetta = cb.or(oikeaKoulutustyyppi, cb.isNull(peruste));
            pred = cb.and(oikeaKoulutustoimija, koulutustyyppiTaiIlmanPerustetta);
        } else {
            // Rajataan koulutustoimijan mukaan
            pred = cb.and(oikeaKoulutustoimija);
        }

        // Rajataan tyypin mukaan
        if (!ObjectUtils.isEmpty(queryDto.getTyyppi())) {
            return cb.and(pred, root.get(Opetussuunnitelma_.tyyppi).in(queryDto.getTyyppi()));
        }

        // Rajataan nimen mukaan
        if (!ObjectUtils.isEmpty(queryDto.getNimi())) {
            String nimi = RepositoryUtil.kuten(queryDto.getNimi());
            Predicate nimessa = cb.like(cb.lower(opsNimi.get(Teksti_.teksti)), cb.literal(nimi));

            Predicate diaarissa = cb.like(cb.lower(root.get(Opetussuunnitelma_.perusteDiaarinumero)), cb.literal(nimi));

            pred = cb.and(pred, cb.or(nimessa, diaarissa));
        }

        // Rajataan tilojen mukaan
        if (!ObjectUtils.isEmpty(queryDto.getTila())) {
            return cb.and(pred, root.get(Opetussuunnitelma_.tila).in(queryDto.getTila()));
        }

        return pred;
    }

}

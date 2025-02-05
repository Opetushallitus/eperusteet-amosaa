package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.custom;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma_;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.*;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaQueryDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaCustomRepository;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KoulutustoimijaRepositoryImpl implements KoulutustoimijaCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Koulutustoimija> findBy(PageRequest page, KoulutustoimijaQueryDto queryDto) {
        TypedQuery<Long> countQuery = getCountQuery(queryDto);
        TypedQuery<Tuple> query = getQuery(queryDto);
        if (page != null) {
            query.setFirstResult(Long.valueOf(page.getOffset()).intValue());
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
        pred = cb.and(pred, cb.or(cb.equal(root.get(Opetussuunnitelma_.tila), Tila.JULKAISTU), cb.isNotEmpty(root.get(Opetussuunnitelma_.julkaisut))));
        pred = cb.and(pred, cb.equal(koulutustoimija.get(Koulutustoimija_.organisaatioRyhma), queryDto.isOrganisaatioRyhma()));

        if (!ObjectUtils.isEmpty(queryDto.getKoulutustyyppi())) {
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste, JoinType.LEFT);
            Predicate oikeatKoulutustyypit = peruste.get(CachedPeruste_.koulutustyyppi).in(queryDto.getKoulutustyyppi());
            Predicate koulutustyyppiTaiIlmanPerustetta = cb.or(
                    oikeatKoulutustyypit,
                    root.get(Opetussuunnitelma_.koulutustyyppi).in(queryDto.getKoulutustyyppi()));
            pred = cb.and(pred, koulutustyyppiTaiIlmanPerustetta);
        }

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

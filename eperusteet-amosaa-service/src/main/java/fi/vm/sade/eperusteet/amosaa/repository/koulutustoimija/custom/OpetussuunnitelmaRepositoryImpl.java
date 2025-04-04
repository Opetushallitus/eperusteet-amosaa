package fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.custom;

import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste_;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.Koulutuskoodi;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.Koulutuskoodi_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale_;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Teksti_;
import fi.vm.sade.eperusteet.amosaa.dto.OpsHakuDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaQueryDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaCustomRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;

public class OpetussuunnitelmaRepositoryImpl implements OpetussuunnitelmaCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Opetussuunnitelma> findBy(PageRequest page, OpetussuunnitelmaQueryDto queryDto) {
        TypedQuery<Long> countQuery = getCountQuery(queryDto);
        TypedQuery<Tuple> query = getQuery(queryDto);
        if (page != null) {
            query.setFirstResult(Long.valueOf(page.getOffset()).intValue());
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
        Predicate eiOleOpsPohja = cb.notEqual(root.get(Opetussuunnitelma_.tyyppi), OpsTyyppi.OPSPOHJA);
        Predicate pred = cb.and(
                cb.or(isJulkaistu, cb.and(cb.notEqual(root.get(Opetussuunnitelma_.tila), Tila.POISTETTU), cb.isNotEmpty(root.get(Opetussuunnitelma_.julkaisut)))),
                onOikeallaKielella, eiOlePohja, eiOleOpsPohja);

        if (!ObjectUtils.isEmpty(queryDto.getOppilaitosTyyppiKoodiUri())) {
            pred = cb.and(pred, cb.equal(root.get(Opetussuunnitelma_.oppilaitosTyyppiKoodiUri), queryDto.getOppilaitosTyyppiKoodiUri()));
        }

        if (!ObjectUtils.isEmpty(queryDto.getKoulutustyyppi())) {
            // Rajataan koulutustoimijan ja koulutustyypin mukaan
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste, JoinType.LEFT);
            Predicate oikeatKoulutustyypit = peruste.get(CachedPeruste_.koulutustyyppi).in(queryDto.getKoulutustyyppi());
            Predicate koulutustyyppiTaiIlmanPerustetta = cb.or(
                    oikeatKoulutustyypit,
                    root.get(Opetussuunnitelma_.koulutustyyppi).in(queryDto.getKoulutustyyppi()));
            pred = cb.and(pred, koulutustyyppiTaiIlmanPerustetta);
        }

        if (queryDto.getNimi() != null && !"".equals(queryDto.getNimi())) {
            String nimi = RepositoryUtil.kuten(queryDto.getNimi());
            Predicate nimessa = cb.like(cb.lower(opsNimi.get(Teksti_.teksti)), cb.literal(nimi));
            Predicate diaarissa = cb.like(cb.lower(root.get(Opetussuunnitelma_.perusteDiaarinumero)), cb.literal(nimi));
            SetJoin<LokalisoituTeksti, Teksti> ktNimi = koulutustoimija.join(Koulutustoimija_.nimi).join(LokalisoituTeksti_.teksti);
            Predicate ktNimessa = cb.like(cb.lower(ktNimi.get(Teksti_.teksti)), cb.literal(nimi));

            Join<Opetussuunnitelma, SisaltoViite> sisaltoviitteet = root.join(Opetussuunnitelma_.sisaltoviitteet, JoinType.LEFT);
            SetJoin<LokalisoituTeksti, Teksti> tutkinnonosaNimi = sisaltoviitteet.join(SisaltoViite_.tekstiKappale).join(TekstiKappale_.nimi).join(LokalisoituTeksti_.teksti);
            Predicate toNimessa = cb.like(cb.lower(tutkinnonosaNimi.get(Teksti_.teksti)), cb.literal(nimi));

            pred = cb.and(pred, cb.or(nimessa, diaarissa, ktNimessa, toNimessa));
        }

        if (queryDto.getTyyppi() != null && !queryDto.getTyyppi().isEmpty()) {
            Predicate tyyppiOn = root.get(Opetussuunnitelma_.tyyppi).in(queryDto.getTyyppi());
            pred = cb.and(pred, tyyppiOn);
        }

        if (queryDto.getPerusteenDiaarinumero() != null && !"".equals(queryDto.getPerusteenDiaarinumero()) && queryDto.getPerusteId() != null) {

            Predicate matchDiaarinumero = cb.equal(root.get(Opetussuunnitelma_.perusteDiaarinumero), queryDto.getPerusteenDiaarinumero());
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste);
            Predicate matchPeruste = cb.equal(peruste.get(CachedPeruste_.perusteId), queryDto.getPerusteId());

            pred = cb.and(pred, cb.or(matchPeruste, matchDiaarinumero));

        } else {
            if (queryDto.getPerusteenDiaarinumero() != null && !"".equals(queryDto.getPerusteenDiaarinumero())) {
                Predicate matchDiaarinumero = cb.equal(root.get(Opetussuunnitelma_.perusteDiaarinumero), queryDto.getPerusteenDiaarinumero());
                pred = cb.and(pred, matchDiaarinumero);
            }

            if (queryDto.getPerusteId() != null) {
                Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste);
                Predicate matchPeruste = cb.equal(peruste.get(CachedPeruste_.perusteId), queryDto.getPerusteId());
                pred = cb.and(pred, matchPeruste);
            }
        }

        if (queryDto.getOrganisaatio() != null && !"".equals(queryDto.getOrganisaatio())) {
            Predicate matchOrganisaatio = cb.equal(koulutustoimija.get(Koulutustoimija_.organisaatio), cb.literal(queryDto.getOrganisaatio()));
            pred = cb.and(pred, matchOrganisaatio);
        } else {
            pred = cb.and(pred, cb.equal(root.join(Opetussuunnitelma_.koulutustoimija).get(Koulutustoimija_.organisaatioRyhma), queryDto.isOrganisaatioRyhma()));
        }

        return pred;
    }

    @Override
    public Page<Opetussuunnitelma> findBy(PageRequest page, OpsHakuDto queryDto) {
        TypedQuery<Long> countQuery = getCountQuery(queryDto);
        TypedQuery<Tuple> query = getQuery(queryDto);
        if (page != null) {
            query.setFirstResult(Long.valueOf(page.getOffset()).intValue());
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
        if (queryDto.getJarjestys() != null) {
            Expression<String> sortExpression;
            if (queryDto.getJarjestys().equals("nimi")) {
                sortExpression = cb.lower(opsNimi.get(Teksti_.teksti));
            } else {
                sortExpression = root.get(queryDto.getJarjestys());
            }

            if (queryDto.isJarjestysNouseva()) {
                orders.add(cb.asc(sortExpression));
            } else {
                orders.add(cb.desc(sortExpression));
            }
        } else {
            orders.add(cb.asc(nimi));
            orders.add(cb.asc(root.get(Opetussuunnitelma_.id)));
        }

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
        Predicate oikeaKoulutustoimija = cb.and();
        if (!ObjectUtils.isEmpty(queryDto.getKoulutustoimijat())) {
            oikeaKoulutustoimija = cb.and(koulutustoimija.get(Koulutustoimija_.id).in(queryDto.getKoulutustoimijat()));
        }

        Predicate pred;
        if (!ObjectUtils.isEmpty(queryDto.getPeruste())) {
            // Rajataan koulutustoimijan ja  perusteen mukaan
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste);
            Predicate oikeaPeruste = cb.equal(peruste.get(CachedPeruste_.id), queryDto.getPeruste());
            pred = cb.and(oikeaKoulutustoimija, oikeaPeruste);
        } else if (!ObjectUtils.isEmpty(queryDto.getKoulutustyyppi())) {
            // Rajataan koulutustoimijan ja koulutustyypin mukaan
            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste, JoinType.LEFT);
            Predicate oikeatKoulutustyypit = peruste.get(CachedPeruste_.koulutustyyppi).in(queryDto.getKoulutustyyppi());
            Predicate koulutustyyppiTaiIlmanPerustetta = cb.or(
                    oikeatKoulutustyypit,
                    root.get(Opetussuunnitelma_.koulutustyyppi).in(queryDto.getKoulutustyyppi()));
            pred = cb.and(oikeaKoulutustoimija, koulutustyyppiTaiIlmanPerustetta);
        } else {
            // Rajataan koulutustoimijan mukaan
            pred = cb.and(oikeaKoulutustoimija);
        }

        // Rajataan tyypin mukaan
        if (!ObjectUtils.isEmpty(queryDto.getTyyppi())) {
            pred = cb.and(pred, root.get(Opetussuunnitelma_.tyyppi).in(queryDto.getTyyppi()));
        }

        // Rajataan nimen mukaan
        if (!ObjectUtils.isEmpty(queryDto.getNimi())) {
            String nimi = RepositoryUtil.kuten(queryDto.getNimi());
            Predicate nimessa = cb.like(cb.lower(opsNimi.get(Teksti_.teksti)), cb.literal(nimi));
            Predicate onOikeallaKielella = cb.equal(opsNimi.get(Teksti_.kieli), Kieli.of(queryDto.getKieli()));
            Predicate nimiKielella = cb.and(nimessa, onOikeallaKielella);

            Predicate diaarissa = cb.like(cb.lower(root.get(Opetussuunnitelma_.perusteDiaarinumero)), cb.literal(nimi));

            Join<Opetussuunnitelma, CachedPeruste> peruste = root.join(Opetussuunnitelma_.peruste, JoinType.LEFT);
            SetJoin<CachedPeruste, Koulutuskoodi> koulutuskoodit =  peruste.join(CachedPeruste_.koulutuskoodit, JoinType.LEFT);
            Predicate koulutuskoodiArvossa = cb.like(cb.lower(koulutuskoodit.get(Koulutuskoodi_.koulutuskoodiArvo)), cb.literal(nimi));

            pred = cb.and(pred, cb.or(nimiKielella, diaarissa, koulutuskoodiArvossa));
        } else {
            pred = cb.and(pred, cb.equal(opsNimi.get(Teksti_.kieli), Kieli.of(queryDto.getKieli())));
        }

        // Rajataan tilojen mukaan
        if (!ObjectUtils.isEmpty(queryDto.getTila())) {
            pred = cb.and(pred, root.get(Opetussuunnitelma_.tila).in(queryDto.getTila()));
        }

        if (!(queryDto.isTuleva() && queryDto.isVoimassaolo() && queryDto.isPoistunut())) {
            if (queryDto.isTuleva()) {
                pred = cb.and(pred, cb.or(
                        root.get(Opetussuunnitelma_.voimaantulo).isNull(),
                        cb.greaterThan(root.get(Opetussuunnitelma_.voimaantulo), new Date())));
            } else if (queryDto.isVoimassaolo()) {
                pred = cb.and(pred, cb.lessThanOrEqualTo(root.get(Opetussuunnitelma_.voimaantulo), new Date()));
                pred = cb.and(pred, cb.or(
                        root.get(Opetussuunnitelma_.voimassaoloLoppuu).isNull(),
                        cb.greaterThan(root.get(Opetussuunnitelma_.voimassaoloLoppuu), new Date())));
            } else if (queryDto.isPoistunut()) {
                pred = cb.and(pred, cb.lessThanOrEqualTo(root.get(Opetussuunnitelma_.voimassaoloLoppuu), new Date()));
            }
        }

        return pred;
    }

}

package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.ReferenceableEntity;

import java.util.function.Predicate;

public final class Util {

    private Util() {
        //util
    }

    /**
     * vertailee kahta samantyyppistä viitettä ja palauttaa true jos kumpikin on null tai kumpikin on ei-null
     */
    public static <T> boolean refXnor(T l, T r) {
        return (l == null && r == null) || (l != null && r != null);
    }

    public static <T extends ReferenceableEntity> boolean identityEquals(T l, T r) {
        return (l != null && r != null && l.getId() != null && l.getId().equals(r.getId()));
    }

    /**
     * Utility for boolean method references
     *
     * @param p
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> not(Predicate<T> p) {
        return p.negate();
    }

    public static <T> Predicate<T> and(Predicate<T> a, Predicate<? super T> b) {
        return a.and(b);
    }

    public static <T> Predicate<T> or(Predicate<T> a, Predicate<? super T> b) {
        return a.or(b);
    }
}

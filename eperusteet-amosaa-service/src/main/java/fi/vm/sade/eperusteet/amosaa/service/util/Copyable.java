package fi.vm.sade.eperusteet.amosaa.service.util;

public interface Copyable<T> {
    default T copy() {
        return copy(true);
    }

    T copy(boolean deep);
}

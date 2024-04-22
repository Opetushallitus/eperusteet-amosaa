package fi.vm.sade.eperusteet.amosaa.domain;

public interface Mergeable<T> {
    void mergeState(T updated);
}

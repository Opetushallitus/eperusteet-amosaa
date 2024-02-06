package fi.vm.sade.eperusteet.amosaa.domain;

public interface PartialMergeable<T> extends Mergeable<T> {
    void partialMergeState(T updated);
}

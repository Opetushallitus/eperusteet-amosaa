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
package fi.vm.sade.eperusteet.amosaa.service.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author mikkom
 */
public class CollectionUtil {
    public static <T> Set<T> intersect(Collection<T> a, Collection<T> b) {
        Set<T> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        return intersection;
    }

    public static <T> Stream<T> treeToStream(Collection<T> roots, Function<T, Collection<T>> mapper) {
        Iterator<T> iterator = new Iterator<T>() {
            final LinkedList<T> stack = new LinkedList<>();

            {
                roots.forEach(r -> {
                    if (!stack.contains(r)) {
                        stack.add(r);
                    }
                });
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public T next() {
                T next = stack.pop();

                mapper.apply(next).forEach(r -> {
                    if (!stack.contains(r)) {
                        stack.add(r);
                    }
                });

                return next;
            }
        };

        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet()).stream();
    }

    public static <T> Stream<T> treeToStream(T root, Function<T, Collection<T>> mapper) {
        return treeToStream(Collections.singleton(root), mapper);
    }

    public static <T, R> R mapRecursive(
            T root,
            Function<T, Collection<T>> getSource,
            Function<R, Collection<R>> getTarget,
            Function<T, R> map) {
        R result = map.apply(root);
        Collection<T> children = getSource.apply(root);
        if (children != null) {
            for (T next : getSource.apply(root)) {
                R r = mapRecursive(next, getSource, getTarget, map);
                getTarget.apply(result).add(r);
            }
        }
        return result;
    }
}

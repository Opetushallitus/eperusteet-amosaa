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
package fi.vm.sade.eperusteet.amosaa.service.mapping;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

/**
 * @author harrik
 */
public class DtoMapperImpl implements DtoMapper {

    private final MapperFacade mapper;

    public DtoMapperImpl(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Override
    public <S, D> D map(S sourceObject, Class<D> destinationClass) {
        return mapper.map(sourceObject, destinationClass);
    }

    @Override
    public <S, D> D map(S sourceObject, D destinationObject) {
        mapper.map(sourceObject, destinationObject);
        return destinationObject;
    }

    @Override
    public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
        return mapper.mapAsList(source, destinationClass);
    }

    @Override
    public <S, D, T extends Collection<D>> T mapToCollection(Iterable<S> source, T dest, Class<D> elemType) {
        mapper.mapAsCollection(source, dest, elemType);
        return dest;
    }

    @Override
    public <M> M unwrap(Class<M> mapperClass) {
        return mapperClass.cast(mapper);
    }
}

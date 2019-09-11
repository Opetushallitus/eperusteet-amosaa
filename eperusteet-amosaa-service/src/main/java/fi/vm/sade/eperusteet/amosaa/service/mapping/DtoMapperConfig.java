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

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import java.time.Instant;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.client.RestClientException;

/**
 * @author jhyoty
 */
@Configuration
public class DtoMapperConfig {

    private static final Logger logger = LoggerFactory.getLogger(DtoMapperConfig.class);

    @Autowired
    OrganisaatioService organisaatioService;

    @Bean
    public DtoMapper dtoMapper(
            ReferenceableEntityConverter referenceableEntityConverter,
            LokalisoituTekstiConverter lokalisoituTekstiConverter,
            KoodistoKoodiConverter koodistoKoodiConverter) {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder()
                .build();

        factory.classMap(RakenneOsaDto.class, SuorituspolkuOsaDto.class)
            .byDefault()
            .register();

        factory.classMap(RakenneModuuliDto.class, SuorituspolkuRakenneDto.class)
                .exclude("osat")
                .byDefault()
                .register();

        factory.getConverterFactory().registerConverter(referenceableEntityConverter);
        factory.getConverterFactory().registerConverter(lokalisoituTekstiConverter);
        factory.getConverterFactory().registerConverter(koodistoKoodiConverter);
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LokalisoituTeksti.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LokalisoituTekstiDto.class));
        factory.getConverterFactory().registerConverter(new PassThroughConverter(Instant.class));
        factory.getConverterFactory().registerConverter(new OrganisaatioConverter());
        OptionalSupport.register(factory);
        factory.registerMapper(new ReferenceableCollectionMergeMapper());

        factory.classMap(Dokumentti.class, DokumenttiDto.class)
                .exclude(Dokumentti_.kansikuva.getName())
                .exclude(Dokumentti_.ylatunniste.getName())
                .exclude(Dokumentti_.alatunniste.getName())
                .byDefault()
                .favorExtension(true)
                .customize(new CustomMapper<Dokumentti, DokumenttiDto>() {
                    @Override
                    public void mapAtoB(Dokumentti dokumentti, DokumenttiDto dokumenttiDto, MappingContext context) {
                        super.mapAtoB(dokumentti, dokumenttiDto, context);
                        dokumenttiDto.setKansikuva(dokumentti.getKansikuva() != null);
                        dokumenttiDto.setYlatunniste(dokumentti.getYlatunniste() != null);
                        dokumenttiDto.setAlatunniste(dokumentti.getAlatunniste() != null);
                    }
                })
                .register();

        factory.classMap(Koulutustoimija.class, KoulutustoimijaDto.class)
                .byDefault()
                .favorExtension(true)
                .customize(new CustomMapper<Koulutustoimija, KoulutustoimijaDto>() {
                    @Override
                    public void mapAtoB(Koulutustoimija source, KoulutustoimijaDto target, MappingContext context) {
                        super.mapBtoA(target, source, context);

                        try {
                            LokalisoituTeksti lokalisoituTeksti = organisaatioService.haeOrganisaatioNimi(source.getOrganisaatio());
                            LokalisoituTekstiDto lokalisoituTekstiDto = new LokalisoituTekstiDto(lokalisoituTeksti.getTekstiAsStringMap());

                            target.setNimi(lokalisoituTekstiDto);
                        } catch (RestClientException | AccessDeniedException ex) {
                            logger.error(ex.getLocalizedMessage());
                        }
                    }
                })
                .register();

        return new DtoMapperImpl(factory.getMapperFacade());
    }

}

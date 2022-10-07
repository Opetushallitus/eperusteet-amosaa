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

import com.google.common.base.Throwables;
import fi.vm.sade.eperusteet.amosaa.domain.Kooditettu;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti_;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OpintokokonaisuusTavoite;
import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaJulkinenDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.KoulutustoimijaYstavaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuRakenneDto;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jhyoty
 */
@Configuration
public class DtoMapperConfig {

    private static final Logger logger = LoggerFactory.getLogger(DtoMapperConfig.class);

    @Autowired
    OrganisaatioService organisaatioService;

    @Autowired
    OpintokokonaisuusTavoiteMapper opintokokonaisuusTavoiteMapper;

    @Lazy
    @Autowired
    KoodistoClient koodistoClient;

    @Bean
    public DtoMapper dtoMapper(
            ReferenceableEntityConverter referenceableEntityConverter,
            LokalisoituTekstiConverter lokalisoituTekstiConverter,
            KoodiConverter koodiConverter,
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
        factory.getConverterFactory().registerConverter(koodiConverter);
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

        factory.classMap(Koulutustoimija.class, KoulutustoimijaBaseDto.class)
                .byDefault()
                .customize(new CustomMapper<Koulutustoimija, KoulutustoimijaBaseDto>() {
                    @Override
                    public void mapAtoB(Koulutustoimija source, KoulutustoimijaBaseDto target, MappingContext context) {
                        super.mapAtoB(source, target, context);

                        try {
                            LokalisoituTeksti lokalisoituTeksti = organisaatioService.haeOrganisaatioNimi(source.getOrganisaatio());
                            LokalisoituTekstiDto lokalisoituTekstiDto = new LokalisoituTekstiDto(lokalisoituTeksti.getTekstiAsStringMap());

                            target.setNimi(lokalisoituTekstiDto);
                        } catch (RestClientException | AccessDeniedException ex) {
                            logger.error(Throwables.getStackTraceAsString(ex));
                        }
                    }
                })
                .register();

        factory.classMap(Koulutustoimija.class, KoulutustoimijaYstavaDto.class)
                .byDefault()
                .register();

        factory.classMap(Koulutustoimija.class, KoulutustoimijaJulkinenDto.class)
                .byDefault()
                .register();

        factory.classMap(Koulutustoimija.class, KoulutustoimijaDto.class)
                .byDefault()
                .register();

        factory.classMap(Koulutustoimija.class, KoulutustoimijaDto.class)
                .byDefault()
                .register();

        factory.classMap(OpintokokonaisuusTavoite.class, OpintokokonaisuusTavoiteDto.class)
                .byDefault()
                .customize(opintokokonaisuusTavoiteMapper)
                .register();

        factory.classMap(Opetussuunnitelma.class, OpetussuunnitelmaDto.class)
                .byDefault()
                .customize(new CustomMapper<Opetussuunnitelma, OpetussuunnitelmaDto>() {
                    @Override
                    public void mapAtoB(Opetussuunnitelma source, OpetussuunnitelmaDto target, MappingContext context) {
                        super.mapAtoB(source, target, context);
                        target.setTila2016(target.getTila());

                        if (!ObjectUtils.isEmpty(source.getOppilaitosTyyppiKoodiUri())) {
                            target.setOppilaitosTyyppiKoodi(koodistoClient.getByUri(source.getOppilaitosTyyppiKoodiUri()));
                        }

                        if (!OpsTyyppi.POHJA.equals(source.getTyyppi()) && !Tila.POISTETTU.equals(source.getTila()) && CollectionUtils.isNotEmpty(source.getJulkaisut())) {
                            target.setTila(Tila.JULKAISTU);
                        }
                    }
                })
                .register();

        factory.classMap(Opetussuunnitelma.class, OpetussuunnitelmaTilastoDto.class)
                .byDefault()
                .customize(new CustomMapper<Opetussuunnitelma, OpetussuunnitelmaTilastoDto>() {
                    @Override
                    public void mapAtoB(Opetussuunnitelma source, OpetussuunnitelmaTilastoDto target, MappingContext context) {
                        super.mapAtoB(source, target, context);
                        if (!OpsTyyppi.POHJA.equals(source.getTyyppi()) && !Tila.POISTETTU.equals(source.getTila()) && CollectionUtils.isNotEmpty(source.getJulkaisut())) {
                            target.setTila(Tila.JULKAISTU);
                        }

                    }
                })
                .register();

        factory.classMap(Opetussuunnitelma.class, OpetussuunnitelmaKaikkiDto.class)
                .byDefault()
                .customize(new CustomMapper<Opetussuunnitelma, OpetussuunnitelmaKaikkiDto>() {
                    @Override
                    public void mapAtoB(Opetussuunnitelma source, OpetussuunnitelmaKaikkiDto target, MappingContext context) {
                        super.mapAtoB(source, target, context);
                        if (!OpsTyyppi.POHJA.equals(source.getTyyppi()) && !Tila.POISTETTU.equals(source.getTila()) && CollectionUtils.isNotEmpty(source.getJulkaisut())) {
                            target.setTila(Tila.JULKAISTU);
                        }

                    }
                })
                .register();

        factory.classMap(Kooditettu.class, KooditettuDto.class)
                .byDefault()
                .favorExtension(true)
                .customize(new CustomMapper<Kooditettu, KooditettuDto>() {
                    @Override
                    public void mapAtoB(Kooditettu source, KooditettuDto target, MappingContext context) {
                        super.mapAtoB(source, target, context);
                        if (source.getUri() != null) {
                            KoodistoKoodiDto koodistokoodi = koodistoClient.getByUri(source.getUri());
                            if (koodistokoodi != null) {
                                Map<String, String> lokalisoitu = Arrays.stream(koodistokoodi.getMetadata()).collect(Collectors.toMap(KoodistoMetadataDto::getKieli, KoodistoMetadataDto::getNimi));
                                target.setKooditettu(new LokalisoituTekstiDto(lokalisoitu));
                            }
                        }
                    }
                })
                .register();

        return new DtoMapperImpl(factory.getMapperFacade());
    }

}

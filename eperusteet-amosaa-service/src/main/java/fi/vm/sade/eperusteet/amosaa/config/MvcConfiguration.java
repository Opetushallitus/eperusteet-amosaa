package fi.vm.sade.eperusteet.amosaa.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.AbstractRakenneOsaDeserializer;
import fi.vm.sade.eperusteet.amosaa.resource.config.MappingModule;
import fi.vm.sade.eperusteet.amosaa.resource.config.ReferenceNamingStrategy;
import fi.vm.sade.eperusteet.amosaa.resource.util.CacheHeaderInterceptor;
import fi.vm.sade.eperusteet.amosaa.resource.util.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.*;

import javax.persistence.EntityManagerFactory;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Autowired
    EntityManagerFactory emf;

    @Override
    public void configurePathMatch(PathMatchConfigurer matcher) {
        matcher.setUseRegisteredSuffixPatternMatch(true);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/ui/").setViewName("forward:/ui/index.html");
        registry.addRedirectViewController("/ui", "/ui/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(byteArrayConverter());
        converters.add(converter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        converters.add(stringHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
        registry.addInterceptor(new CacheHeaderInterceptor());
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setPrettyPrint(true);
        converter.getObjectMapper().enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        converter.getObjectMapper().enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        converter.getObjectMapper().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.getObjectMapper().disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        converter.getObjectMapper().registerModule(new Jdk8Module());
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        MappingModule module = new MappingModule();
        module.addDeserializer(AbstractRakenneOsaDto.class, new AbstractRakenneOsaDeserializer());
        converter.getObjectMapper().registerModule(module);
        converter.getObjectMapper().setPropertyNamingStrategy(new ReferenceNamingStrategy());
        return converter;
    }

    @Bean
    ByteArrayHttpMessageConverter byteArrayConverter() {
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes(Arrays.asList("application/pdf", "image/jpeg", "image/png")));
        return converter;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // numbers chosen by magic-random wizardry. please fix as needed.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20); // overkills ftw
        executor.setThreadFactory(new CustomizableThreadFactory("AsyncThreadFactory-"));
        executor.afterPropertiesSet();

        configurer.setTaskExecutor(executor).setDefaultTimeout(120000);
    }
}

package fi.vm.sade.eperusteet.amosaa.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidOsaamistavoiteEsitietoValidator.class)
@Documented
public @interface ValidOsaamistavoiteEsitieto {
    String message() default "osaamistavoite-esitieto-virhe";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

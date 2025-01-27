package fi.vm.sade.eperusteet.amosaa.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validaattori arvoalueelle
 *
 *
 * {@code @ValidRange(low = 10, high = 42) private Integer arvo;}
 * {@code @ValidRange(low = 10, high = 42) private Long arvo;}
 * {@code @ValidRange(low = 10, high = 42) private String arvo;}
 */
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidStringRangeValidator.class, ValidNumberRangeValidator.class})
@Documented
public @interface ValidRange {
    int low();
    int high();
    String message() default "virheellinen-arvo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

abstract class ValidRangeValidator<T> implements ConstraintValidator<ValidRange, T> {
    private int low = 0;
    private int high = 0;

    @Override
    public void initialize(ValidRange range) {
        low = range.low();
        high = range.high();
    }

    @Override
    public boolean isValid(T arvo, ConstraintValidatorContext context) {
        Integer value = getValue(arvo);
        return value != null && value >= low && value <= high;
    }

    abstract protected Integer getValue(T val);
}

class ValidStringRangeValidator extends ValidRangeValidator<String> {
    @Override
    protected Integer getValue(String val) {
        try {
            return Integer.valueOf(val);
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }
}

class ValidNumberRangeValidator extends ValidRangeValidator<Number> {
    @Override
    protected Integer getValue(Number val) {
        return val.intValue();
    }
}

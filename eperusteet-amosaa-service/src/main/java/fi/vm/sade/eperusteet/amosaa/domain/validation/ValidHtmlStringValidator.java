package fi.vm.sade.eperusteet.amosaa.domain.validation;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author isaul
 */
public class ValidHtmlStringValidator implements ConstraintValidator<ValidHtml, String> {

    private Whitelist whitelist;

    @Override
    public void initialize(ValidHtml constraintAnnotation) {
        whitelist = constraintAnnotation.whitelist().getWhitelist();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || Jsoup.isValid(s, whitelist);
    }
}

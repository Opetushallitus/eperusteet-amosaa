package fi.vm.sade.eperusteet.amosaa.domain.validation;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidHtmlStringValidator implements ConstraintValidator<ValidHtml, String> {

    private Safelist whitelist;

    @Override
    public void initialize(ValidHtml constraintAnnotation) {
        whitelist = constraintAnnotation.whitelist().getWhitelist();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || Jsoup.isValid(s, whitelist);
    }
}

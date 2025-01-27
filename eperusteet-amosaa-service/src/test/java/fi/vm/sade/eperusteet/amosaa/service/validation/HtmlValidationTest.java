package fi.vm.sade.eperusteet.amosaa.service.validation;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.amosaa.domain.validation.ValidHtmlValidatorBase;
import org.junit.Ignore;
import org.junit.Test;

import jakarta.validation.Payload;
import java.lang.annotation.Annotation;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore("linkkivalidointi otettu pois kaytosta")
public class HtmlValidationTest {

    @Test
    public void validLinkPassesValidation() {
        TestHtmlvalidator htmlvalidator = new TestHtmlvalidator();
        htmlvalidator.initialize();
        boolean htmlIsValid = htmlvalidator.isValid(
                "<a href=\"https://www.google.fi/\" target=\"_blank\" rel=\"noopener noreferrer nofollow\">Linkin teksti</a>");
        assertThat(htmlIsValid).isTrue();
    }

    @Test
    public void validLinkSpacesValidation() {
        TestHtmlvalidator htmlvalidator = new TestHtmlvalidator();
        htmlvalidator.initialize();
        boolean htmlIsValid = htmlvalidator.isValid(
                "<a href=\"   https://www.google.fi/    \" target=\"_blank\" rel=\"noopener noreferrer nofollow\">Linkin teksti</a>");
        assertThat(htmlIsValid).isTrue();
    }

    @Test
    public void invalidLinkFailsValidation() {
        TestHtmlvalidator htmlvalidator = new TestHtmlvalidator();
        htmlvalidator.initialize();
        boolean htmlIsValid = htmlvalidator.isValid(
                "<a href=\"#_ftnref3\" target=\"_blank\" rel=\"noopener noreferrer nofollow\">[3]</a>");
        assertThat(htmlIsValid).isFalse();
    }

    @Test
    public void invalidLinkSpacesValidation() {
        TestHtmlvalidator htmlvalidator = new TestHtmlvalidator();
        htmlvalidator.initialize();
        boolean htmlIsValid = htmlvalidator.isValid(
                "<a href=\"https://www.   google.fi/\" target=\"_blank\" rel=\"noopener noreferrer nofollow\">Linkin teksti</a>");
        assertThat(htmlIsValid).isFalse();
    }

    public static class TestHtmlvalidator extends ValidHtmlValidatorBase {
        public boolean isValid(String teksti) {
            HashMap<Kieli, String> tekstiJaKieli = new HashMap<>();
            tekstiJaKieli.put(Kieli.SV, teksti);

            return isValid(LokalisoituTeksti.of(tekstiJaKieli));
        }

        protected void initialize() {
            setupValidator(new ValidHtmlHelper());
        }
    }

    public static class ValidHtmlHelper implements ValidHtml {
        @Override
        public String message() {
            return null;
        }

        @Override
        public WhitelistType whitelist() {
            return WhitelistType.NORMAL;
        }

        @Override
        public Class<?>[] groups() {
            return null;
        }

        @Override
        public Class<? extends Payload>[] payload() {
            return null;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }
    }
}

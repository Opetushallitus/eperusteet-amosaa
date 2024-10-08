package fi.vm.sade.eperusteet.amosaa.domain.validation;

import com.google.common.base.CharMatcher;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.util.Map;

import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ValidHtmlValidatorBase {

    private Safelist whitelist;
    private final UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    private final EmailValidator emailValidator = EmailValidator.getInstance(true, true);

    private static final Logger logger = LoggerFactory.getLogger(ValidHtmlValidatorBase.class);

    protected void setupValidator(ValidHtml constraintAnnotation) {
        whitelist = constraintAnnotation.whitelist().getWhitelist();
    }

    protected boolean isValid(LokalisoituTeksti lokalisoituTeksti) {
        if (lokalisoituTeksti == null) {
            return true;
        }

        Map<Kieli, String> tekstit = lokalisoituTeksti.getTeksti();

        if (tekstit == null) {
            return true;
        }

        boolean htmlIsValid = tekstit.values().stream()
                .allMatch(teksti -> Jsoup.isValid(teksti, whitelist));

        if (!htmlIsValid) {
            logger.error("html:n validointi epäonnistui, {}", lokalisoituTeksti.getTunniste());
        }

        return htmlIsValid;
    }

    @Deprecated
    private boolean isValidUrls(String teksti) {
        Document doc = Jsoup.parse(teksti);
        Elements links = doc.select("a[href]");
        return links.stream().allMatch(link ->
                !link.attr("routenode").isEmpty()
                        || urlValidator.isValid(CharMatcher.whitespace().trimFrom(link.attr("abs:href")))
                        || emailValidator.isValid(CharMatcher.whitespace().trimFrom(link.attr("href").replace("mailto:", "")))
        );
    }
}

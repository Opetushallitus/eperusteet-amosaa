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
package fi.vm.sade.eperusteet.amosaa.domain.validation;

import org.jsoup.safety.Whitelist;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author mikkom
 */
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidHtmlValidator.class, ValidHtmlCollectionValidator.class, ValidHtmlStringValidator.class})
@Documented
public @interface ValidHtml {

    String message() default "ei-validia-html";

    WhitelistType whitelist() default WhitelistType.NORMAL;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    enum WhitelistType {
        NONE(Whitelist.none()),
        MINIMAL(Whitelist.none().addTags("p")),
        SIMPLIFIED(Whitelist.none().addTags("p", "strong", "em", "i", "s", "ol", "li", "ul")),
        NORMAL(getNormalWhiteList()),
        NORMAL_PDF(getNormalWhiteList().removeAttributes("a", "routenode"));

        private final Whitelist whitelist;

        WhitelistType(Whitelist whitelist) {
            this.whitelist = whitelist;
        }

        public Whitelist getWhitelist() {
            return whitelist;
        }

        private static Whitelist getNormalWhiteList() {
            return Whitelist.none()
                    .addTags("p", "span", "strong", "em", "i", "s", "ol", "li", "ul", "blockquote", "table", "caption",
                            "tbody", "tr", "td", "hr", "pre", "th", "thead", "a", "abbr", "comment", "figcaption", "br",
                            "dt", "dl", "dd")
                    .addAttributes("table", "align", "border", "cellpadding", "cellspacing", "style", "summary")
                    .addAttributes("th", "scope", "colspan", "rowspan", "style")
                    .addAttributes("td", "colspan", "rowspan", "style", "data-colwidth")
                    .addAttributes("a", "href", "target", "rel", "routenode")
                    .addAttributes("img", "data-uid", "src", "alt", "height", "width", "style", "figcaption")
                    .addAttributes("abbr", "data-viite")
                    .addAttributes("figure", "class")
                    .addAttributes("span", "kommentti", "class");
        }
    }
}

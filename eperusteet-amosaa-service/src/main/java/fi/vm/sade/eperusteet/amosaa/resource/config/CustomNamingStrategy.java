package fi.vm.sade.eperusteet.amosaa.resource.config;

import org.hibernate.cfg.EJB3NamingStrategy;

/**
 * @author isaul
 */
public class CustomNamingStrategy extends EJB3NamingStrategy {

    @Override
    public String tableName(String tableName) {
        return convertUpperCamel2LowerUnder(tableName);
    }

    @Override
    public String columnName(String columnName) {
        return convertUpperCamel2LowerUnder(columnName);
    }

    private String convertUpperCamel2LowerUnder(String str) {
        StringBuilder builder = new StringBuilder(str);
        for (int i = 1; i < str.length() - 1; i++) {
            if (Character.isUpperCase(str.charAt(i)) && Character.isLowerCase(str.charAt(i + 1))) {
                builder.setCharAt(i, str.charAt(i));
                builder.insert(i, "_");
            }
        }

        return builder.toString().toLowerCase();
    }
}

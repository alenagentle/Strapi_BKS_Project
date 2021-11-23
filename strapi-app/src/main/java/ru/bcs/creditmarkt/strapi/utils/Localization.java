package ru.bcs.creditmarkt.strapi.utils;

import ru.bcs.creditmarkt.strapi.utils.constants.LocaleConstants;

import java.util.ResourceBundle;

public class Localization {

    public static ResourceBundle getMessageBundle() {
        return ResourceBundle.getBundle(
                "messages",
                LocaleConstants.DEFAULT_LOCALE,
                new UTF8Control()
        );
    }

}

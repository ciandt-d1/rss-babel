package com.ciandt.d1.rssbabel.utils;

import com.google.cloud.RetryParams;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle language translation
 */
public class TranslateUtils {

    private static final Logger logger = LoggerFactory.getLogger(TranslateUtils.class.getName());

    /**
     * Translate from one language to another
     *
     * @param text text to translate
     */
    public static String translate(String text) {

        // Instantiates a service client
        Translate translate = createTranslateService();

        // Translates some text into English
        Translation translation = translate.translate(text);


        if (logger.isDebugEnabled()) {
            logger.debug("Text: " + text);
            logger.debug("Translation: " + translation.getTranslatedText());
        }

        return translation.getTranslatedText();
    }

    /**
     * Create Google Translate API Service.
     *
     * @return Google Translate Service
     */
    public static Translate createTranslateService() {
        TranslateOptions translateOption = TranslateOptions.newBuilder()
                .setRetryParams(retryParams())
                .setConnectTimeout(60000)
                .setReadTimeout(60000)
                .build();

        return translateOption.getService();
    }

    /**
     * Retry params for the Translate API.
     */
    private static RetryParams retryParams() {
        return RetryParams.newBuilder()
                .setRetryMaxAttempts(3)
                .setMaxRetryDelayMillis(30000)
                .setTotalRetryPeriodMillis(120000)
                .setInitialRetryDelayMillis(250)
                .build();
    }
}

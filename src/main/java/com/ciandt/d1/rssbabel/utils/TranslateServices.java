package com.ciandt.d1.rssbabel.utils;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Handle language translation
 */
@Service
public class TranslateServices {

    private static final Logger logger = LoggerFactory.getLogger(TranslateServices.class.getName());

    private static final String BASE_URL = "https://translation.googleapis.com/language/translate/v2";

    @Autowired
    private LogServices logServices;

    @Autowired
    private Environment env;

    /**
     * Translate from one language to another
     *
     * @param text text to translate
     */
    public String translate(String text, String targetLanguage) {

        String key = env.getProperty("key");
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("You must provide an API KEY for Google Translate under system property named 'key'");
        }

        Client client = ClientBuilder.newClient().register(JacksonFeature.class);
        TranslationResult translationResult = null;
        try {
            /*
            translationResult = client.target(BASE_URL)
                    .queryParam("q", URLEncoder.encode(text, "UTF-8"))
                    .queryParam("key", key)
                    .queryParam("model", "nmt")
                    .queryParam("target", targetLanguage)
                    .queryParam("format", "html")
                    .request()
                    .header("Content-Length", "0")
                    .post(Entity.text(""), TranslationResult.class);
                    */

            WebTarget webTarget = client.target(BASE_URL);
            MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
            formData.add("q", URLEncoder.encode(text, "UTF-8"));
            formData.add("key", key);
            formData.add("model", "nmt");
            formData.add("target", targetLanguage);
            formData.add("format", "html");
            translationResult = webTarget.request().post(Entity.form(formData), TranslationResult.class);

        } catch (UnsupportedEncodingException e) {
            logServices.fatal(logger, "Error encoding parameters", e);
        }

        String result = null;

        if ((translationResult.getData().getTranslations() != null) &&
                (translationResult.getData().getTranslations().size() > 0)) {

            result = translationResult.getData().getTranslations().get(0).getTranslatedText();
        } else {
            logger.debug("API call didn't return anything");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Text: " + text);
            logger.debug("Translation: " + result);
        }

        return result;
    }

    /**
     * List supported languages
     */
    public List<Language> listSupportedLanguages() {
        Translate translate = createTranslateService(null);
        List<Language> languages = translate.listSupportedLanguages();

        /*
        if (logger.isDebugEnabled()) {
            for (Language language : languages) {
                logger.debug( "Code = " + language.getCode() + ", Name = " + language.getName());
            }
        }
        */

        return languages;
    }

    /**
     * Create Google Translate API Service.
     *
     * @return Google Translate Service
     */
    public static Translate createTranslateService() {
        return TranslateServices.createTranslateService(null);
    }

    /**
     * Create Google Translate API Service.
     *
     * @return Google Translate Service
     */
    private static Translate createTranslateService(String apiKey) {

        Translate translate = null;

        if (apiKey != null) {
            TranslateOptions.Builder optionsBuilder = TranslateOptions.newBuilder();
            optionsBuilder.setApiKey(apiKey);
            translate = optionsBuilder.build().getService();
        } else {
            translate = TranslateOptions.getDefaultInstance().getService();
        }

        return translate;
    }

    private static class TranslationResult {
        private TranslationData data;

        public TranslationData getData() {
            return data;
        }

        public void setData(TranslationData data) {
            this.data = data;
        }
    }

    private static class TranslationData {
        private List<TranslateServices.Translation> translations;

        public List<Translation> getTranslations() {
            return translations;
        }

        public void setTranslations(List<Translation> translations) {
            this.translations = translations;
        }
    }

    private static class Translation {
        private String translatedText;
        private String detectedSourceLanguage;
        private String model;

        public String getTranslatedText() {
            return translatedText;
        }

        public void setTranslatedText(String translatedText) {
            this.translatedText = translatedText;
        }

        public String getDetectedSourceLanguage() {
            return detectedSourceLanguage;
        }

        public void setDetectedSourceLanguage(String detectedSourceLanguage) {
            this.detectedSourceLanguage = detectedSourceLanguage;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }
}

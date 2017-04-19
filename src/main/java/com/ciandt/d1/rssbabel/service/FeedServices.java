package com.ciandt.d1.rssbabel.service;

import com.ciandt.d1.rssbabel.config.GeneralConfig;
import com.ciandt.d1.rssbabel.utils.CacheServices;
import com.ciandt.d1.rssbabel.utils.LogServices;
import com.ciandt.d1.rssbabel.utils.TranslateServices;
import com.google.cloud.translate.Language;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Handles feed requests and processing
 */
@Service
public class FeedServices {

    private static Logger logger = LoggerFactory.getLogger(FeedServices.class.getName());

    private final LogServices logServices;
    private final TranslateServices translateServices;
    private final GeneralConfig generalConfig;
    private final CacheServices cacheServices;

    //TODO: change to a cache strategy with timeout
    private List<Language> languageList;
    private String supportedLanguages;

    @Autowired
    public FeedServices(LogServices logServices, TranslateServices translateServices, GeneralConfig generalConfig,
                        CacheServices cacheServices) {
        this.logServices = logServices;
        this.translateServices = translateServices;
        this.generalConfig = generalConfig;
        this.cacheServices = cacheServices;
    }

    /**
     * Check if a language is supported by Google Translate API or not
     */
    public boolean isLanguageSupported(String language) {
        for (Language supportedLanguage : getLanguageList()) {
            if (language.equals(supportedLanguage.getCode())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return a list of supported languages
     */
    public String supportedLanguages() {
        return supportedLanguages;
    }

    /**
     * Read the content of a feed and returns a new feed with translated text
     */
    public String translateFeed(String originalFeedUrl, String targetLanguage) throws IOException, FeedException {
        logger.debug("Translating feed " + originalFeedUrl + " to language = " + targetLanguage);

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(new URL(originalFeedUrl)));
        List<SyndEntry> syndEntryList = feed.getEntries();
        List<SyndEntry> newSyndEntryList = new ArrayList<>();

        //Feed title
        String languageName = getLanguageName(targetLanguage);
        feed.setTitle(feed.getTitle() + " (" + languageName + ")");

        //Feed description
        if (!StringUtils.isEmpty(feed.getDescription())) {
            feed.setDescription(translateServices.translate(feed.getDescription(), targetLanguage));
        }

        for (SyndEntry syndEntry : syndEntryList) {

            if (logger.isDebugEnabled()) {
                logger.debug("-------------------");
                logger.debug("Title = " + syndEntry.getTitle());
                logger.debug("Author = " + syndEntry.getAuthor());
                logger.debug("Link = " + syndEntry.getLink());
                logger.debug("-------------------");
            }

            String cacheKey = generateUniqueId(syndEntry, targetLanguage);
            SyndEntry convertedSyndEntry = cacheServices.get(cacheKey, new SyndEntryLoader(syndEntry, targetLanguage));
            newSyndEntryList.add(convertedSyndEntry);
        }

        //change the entries to the converted one
        feed.setEntries(newSyndEntryList);

        Writer writer = new StringWriter();
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, writer);
        writer.flush();
        writer.close();

        return writer.toString();
    }

    /**
     * Return the language list
     */
    private List<Language> getLanguageList() {
        if (languageList == null) {
            languageList = translateServices.listSupportedLanguages();

            StringBuffer sb = new StringBuffer();
            for (Language language : languageList) {
                sb.append(language.getCode() + " - " + language.getName() + "\n");
            }
            supportedLanguages = sb.toString();
        }
        return languageList;
    }

    /**
     * Generate an unique ID for the entry and target language
     */
    String generateUniqueId(SyndEntry syndEntry, String targetLanguage) {
        String key = null;

        if (syndEntry.getLink() != null) {
            key = syndEntry.getLink() + targetLanguage;
        } else {
            key = syndEntry.getTitle() + targetLanguage;
        }

        long h = 98764321261L;
        int l = key.length();
        char[] chars = key.toCharArray();

        for (int i = 0; i < l; i++) {
            h = 31 * h + chars[i];
        }

        return String.valueOf(h);
    }

    /**
     * Convert the content to another language
     */
    private class SyndEntryLoader implements Callable<SyndEntry> {
        private SyndEntry syndEntry;
        private String targetLanguage;

        public SyndEntryLoader(SyndEntry syndEntry, String targetLanguage) {
            this.syndEntry = syndEntry;
            this.targetLanguage = targetLanguage;
        }

        @Override
        public SyndEntry call() throws Exception {

            try {
                if (!StringUtils.isEmpty(syndEntry.getTitle())) {
                    syndEntry.setTitle(translateServices.translate(syndEntry.getTitle(), targetLanguage));
                }
                List<SyndContent> contentList = syndEntry.getContents();
                for (SyndContent content : contentList) {
                    if (!StringUtils.isEmpty(content.getValue())) {
                        content.setValue(translateServices.translate(content.getValue(), targetLanguage));
                    }
                }
                if (syndEntry.getDescription() != null) {
                    SyndContent description = syndEntry.getDescription();
                    if (!StringUtils.isEmpty(description.getValue())) {
                        description.setValue(translateServices.translate(description.getValue(), targetLanguage));
                    }
                }
            } catch (Exception exc) {
                logServices.warn(logger, "Error converting entry with title " + syndEntry.getTitle(), exc);
            }

            return syndEntry;
        }
    }

    /**
     * Return the language name
     */
    private String getLanguageName(String strLang) {
        List<Language> languageList = getLanguageList();

        for (Language language : languageList) {
            if (strLang.equals(language.getCode())) {
                return language.getName();
            }
        }

        return null;
    }
}

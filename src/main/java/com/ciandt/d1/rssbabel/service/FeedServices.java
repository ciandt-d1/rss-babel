package com.ciandt.d1.rssbabel.service;

import com.ciandt.d1.rssbabel.config.GeneralConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.List;

/**
 * Handles feed requests and processing
 */
@Service
public class FeedServices {

    private static Logger logger = LoggerFactory.getLogger(FeedServices.class.getName());

    private final LogServices logServices;
    private final TranslateServices translateServices;
    private final GeneralConfig generalConfig;

    //TODO: change to a cache strategy with timeout
    private List<Language> languageList;
    private String supportedLanguages;

    @Autowired
    public FeedServices(LogServices logServices, TranslateServices translateServices, GeneralConfig generalConfig) {
        this.logServices = logServices;
        this.translateServices = translateServices;
        this.generalConfig = generalConfig;
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

        for (SyndEntry syndEntry : syndEntryList) {
            List<SyndContent> contentList = syndEntry.getContents();
            for (SyndContent content : contentList) {
                content.setValue(translateServices.translate(content.getValue(), targetLanguage));
            }
        }

        if (logger.isDebugEnabled()) {
            for (SyndEntry syndEntry : syndEntryList) {
                logger.debug("-------------------");
                logger.debug("Title = " + syndEntry.getTitle());
                logger.debug("Author = " + syndEntry.getAuthor());
                logger.debug("-------------------");
            }
        }

        Writer writer = new StringWriter();
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, writer);
        writer.flush();
        writer.close();


        /*
        Abdera abdera = new Abdera();
        Parser parser = abdera.getParser();

        URL url = new URL(originalFeedUrl);
        Document<Feed> doc = parser.parse(url.openStream(), url.toString());
        Feed feed = doc.getRoot();

        if (logger.isDebugEnabled()) {
            logger.debug("Feed found. Feed Title: " + feed.getTitle());
            for (Entry entry : feed.getEntries()) {
                logger.debug("\tEntry Title: " + entry.getTitle());
            }
            logger.debug("Feed Author: " + feed.getAuthor());
        }
        */

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


}

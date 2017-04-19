package com.ciandt.d1.rssbabel.controller;

import com.ciandt.d1.rssbabel.config.GeneralConfig;
import com.ciandt.d1.rssbabel.service.FeedServices;
import com.ciandt.d1.rssbabel.utils.LogServices;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Class that creates and delivers content in different languages
 */
@RestController
@RequestMapping(value = "/rss-babel/v1/feed")
public class FeedController {

    private static Logger logger = LoggerFactory.getLogger(FeedController.class.getName());

    private final LogServices logServices;
    private final GeneralConfig generalConfig;
    private final FeedServices feedServices;

    @Autowired
    public FeedController(LogServices logServices, GeneralConfig generalConfig, FeedServices feedServices) {
        this.logServices = logServices;
        this.generalConfig = generalConfig;
        this.feedServices = feedServices;
    }

    /**
     * Generate a feed using a specific language
     */
    @RequestMapping(value = "/{language}", method = RequestMethod.GET, produces = "application/xml")
    public ResponseEntity generateFeed(@PathVariable("language") String language,
                                       @RequestParam(value = "feed", required = false) String feed) {

        //validate the language
        if (!feedServices.isLanguageSupported(language)) {
            return ResponseEntity.badRequest().body("Language " + language + " not supported. Supported languages are:\n"
                    + feedServices.supportedLanguages());
        }

        if (StringUtils.isEmpty(feed)) {
            feed = generalConfig.getDefaultFeed();
        }

        logger.debug("Generating feed for language = " + language + ", and feed = " + feed);
        String result = null;

        try {
            result = feedServices.translateFeed(feed, language);
        } catch (Exception exc) {
            String errorMessage = "Error generating feed based on " + feed + " for language " + language;
            logServices.fatal(logger, errorMessage, exc);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }

        return ResponseEntity.ok(result);
    }


}

package com.ciandt.d1.rssbabel.service;

import com.ciandt.d1.rssbabel.config.GeneralConfig;
import com.ciandt.d1.rssbabel.utils.LogServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles feed requests and processing
 */
@Service
public class FeedServices {

    private static Logger logger = LoggerFactory.getLogger(FeedServices.class.getName());

    private final LogServices logServices;
    private final GeneralConfig generalConfig;

    @Autowired
    public FeedServices(LogServices logServices, GeneralConfig generalConfig) {
        this.logServices = logServices;
        this.generalConfig = generalConfig;
    }

}

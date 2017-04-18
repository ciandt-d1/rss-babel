package com.ciandt.d1.rssbabel.utils;

import com.ciandt.d1.rssbabel.config.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class to handle log services
 */
@Service
public class LogServices {

    private static Logger logger = LoggerFactory.getLogger(LogServices.class.getName());
    private final GeneralConfig generalConfig;

    @Autowired
    public LogServices(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    /**
     * Logs a asynchronous execution
     */
    /**
     * Logs an asynchronous execution
     */
    public static void logAsyncExecution(Logger logger, AbstractRunnable runnable, String message, Long executionTime,
                                         boolean success, Integer retries) {
        logger.info("(Retry #" + retries + "): Job " + runnable.getJobName() + " finished its execution in "
                + executionTime + " msecs with success = " + success + ". Message = " + message);
    }

    /**
     * Logs a fatal exception, that also sends an email to report the issue
     */
    public void fatal(Logger logger, String message) {
        this.fatal(logger, message, null);
    }

    /**
     * Logs a fatal exception, that also sends an email to report the issue
     */
    public void fatal(Logger logger, String message, Throwable t) {
        //logs the error
        logger.error(message, t);
    }

    /**
     * Warns that something unusual is happening
     */
    public void warn(Logger logger, String message) {
        this.warn(logger, message, null);
    }

    /**
     * Warns that something unusual is happening
     */
    public void warn(Logger logger, String message, Throwable t) {

        //logs the error
        logger.warn(message);
    }

}

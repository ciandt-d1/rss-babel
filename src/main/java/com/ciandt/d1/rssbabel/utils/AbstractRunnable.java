package com.ciandt.d1.rssbabel.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Standard runnable for all asynchronous taks
 */
public abstract class AbstractRunnable implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(AbstractRunnable.class.getName());

    public static final Integer DEFAULT_RETRY = 3;
    public static final Integer DEFAULT_RETRY_INTERVAL = 10; //seconds

    private static final Integer retry = 3;
    private final LogServices logServices;

    /**
     * Constructor
     */
    public AbstractRunnable(LogServices logServices) {
        this.logServices = logServices;
    }

    @Override
    public void run() {

        long initTime = System.currentTimeMillis();
        logger.info("Executing asynchronous job " + getJobName());

        int tentative = 0;
        while (tentative <= retry) {
            try {
                this.execute();
                long endTime = System.currentTimeMillis();
                logServices.logAsyncExecution(logger, this, "OK", endTime - initTime, true, tentative);
                break;
            } catch (Exception exc) {
                tentative++;
                if (tentative <= retry) {
                    try {
                        TimeUnit.SECONDS.sleep(DEFAULT_RETRY_INTERVAL * (tentative - 1));
                        logger.warn("Execution of " + getJobName() + " has failed due to " + exc.getMessage()
                                + ". Retrying #" + tentative);
                    } catch (InterruptedException e) {
                        logger.error("Error in the async mechanism", e);
                    }
                } else {
                    long endTime = System.currentTimeMillis();
                    logServices.logAsyncExecution(logger, this, exc.getMessage(), endTime - initTime, false, tentative - 1);
                    logServices.fatal(logger, exc.getMessage(), exc);
                }
            }
        }
    }

    /**
     * Executes the job
     */
    public abstract void execute() throws Exception;

    /**
     * Gets the name of the job
     */
    public abstract String getJobName();
}


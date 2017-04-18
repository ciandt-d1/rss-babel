package com.ciandt.d1.rssbabel.utils;

import com.ciandt.d1.rssbabel.config.GeneralConfig;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Cache methods
 */
@Service
public class CacheServices {

    //4 hours
    private static final Integer DEFAULT_TIMEOUT = 60 * 60 * 4;

    private static Logger logger = LoggerFactory.getLogger(CacheServices.class.getName());

    private MemcachedClient memcachedClient;
    private final GeneralConfig generalConfig;
    private final LogServices logServices;

    /**
     * Constructor
     */
    @Autowired
    private CacheServices(GeneralConfig generalConfig, LogServices logServices) {
        this.generalConfig = generalConfig;
        this.logServices = logServices;
        createCacheClient();
    }

    /**
     * Stores an object into the cache
     */
    public void set(String key, Integer timeout, Object value) {

        if (value == null) {
            return;
        }

        try {
            Object storedValue = memcachedClient.set(key, timeout, value).get();
            if (storedValue == null) {
                logServices.fatal(logger, "Memcached returned a null object after trying to store object " + value
                        + " under key = " + key, null);
            }
        } catch (InterruptedException e) {
            checkCacheClient();
            logServices.fatal(logger, "Error putting object " + value + " into memcached under key = " + key, e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            checkCacheClient();
            logServices.fatal(logger, "Error putting object " + value + " into memcached under key = " + key, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Stores an object into the cache
     */
    public void set(String key, Object value) {
        this.set(key, DEFAULT_TIMEOUT, value);
    }

    /**
     * Retrieves an object from cache
     */
    public <T> T get(String key) {

        Object value = null;

        try {
            value = memcachedClient.get(key);
        } catch (Exception exc) {
            checkCacheClient();
            logServices.fatal(logger, "Error getting object " + value + " into memcached under key = " + key, exc);
            throw new RuntimeException(exc);
        }

        if (value == null) {
            return null;
        }

        return (T) value;
    }

    /**
     * Retrieves an object from cache
     */
    public <T> T get(String key, Callable<? extends T> valueLoader) {

        Object value = null;

        try {
            value = memcachedClient.get(key);
        } catch (Exception exc) {
            checkCacheClient();
            logServices.fatal(logger, "Error getting object " + value + " into memcached under key = " + key, exc);
            throw new RuntimeException(exc);
        }

        if (value == null) {
            try {
                value = valueLoader.call();
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
            this.set(key, value);
        }

        return (T) value;
    }

    /**
     * Removes this key from cache
     */
    public void invalidate(String key) {
        try {
            memcachedClient.delete(key);
        } catch (Exception exc) {
            checkCacheClient();
            logServices.fatal(logger, "Error invalidating cache for key = " + key, exc);
            throw new RuntimeException(exc);
        }
    }

    /**
     * Cleans the cache
     */
    public void invalidateAll() {
        try {
            memcachedClient.flush();
        } catch (Exception exc) {
            checkCacheClient();
            logServices.fatal(logger, "Error invalidating cache [all]", exc);
            throw new RuntimeException(exc);
        }
    }

    /**
     * Recreates the cache client
     */
    public void createCacheClient() {
        //creates the client
        String strAddresses = generalConfig.getMemcachedServers();
        logger.info("Initializing cache for servers: " + strAddresses);
        try {
            memcachedClient = new MemcachedClient(AddrUtil.getAddresses(strAddresses));
        } catch (IOException e) {
            logServices.fatal(logger, "Error creating memcachedClient: " + e.getMessage(), e);
        }
    }

    /**
     * Checks the cache client
     */
    public void checkCacheClient() {
        boolean problem = false;
        for (MemcachedNode node : memcachedClient.getNodeLocator().getAll()) {
            if (!node.isActive()) {
                memcachedClient.shutdown();
                problem = true;
            }
        }

        if (problem) {
            createCacheClient();
        }
    }
}


package com.ciandt.d1.rssbabel.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that handles asynchronous executions
 */
public class ExecutorUtils {

    private static ExecutorService executorService;
    private static ExecutorService executorServiceSingleThread;

    private static final Integer THREAD_POOL_SIZE = 5;

    static {
        executorService = Executors.newFixedThreadPool(5);
        executorServiceSingleThread = Executors.newSingleThreadExecutor();
    }

    /**
     * Executes a service in background
     */
    public static void execute(Runnable command) {
        executorService.execute(command);
    }

    /**
     * Executes a service in a single thread
     */
    public static void executeSingleThread(Runnable command) {
        executorServiceSingleThread.execute(command);
    }
}


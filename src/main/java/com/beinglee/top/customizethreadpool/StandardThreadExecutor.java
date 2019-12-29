package com.beinglee.top.customizethreadpool;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class StandardThreadExecutor implements Executor {

    private ThreadPoolExecutor executor = null;

    private int maxQueueSize = Integer.MAX_VALUE;

    private String namePrefix = "customize-exec-";

    private boolean daemon = true;

    private int threadPriority = Thread.NORM_PRIORITY;

    private String name;

    private int maxThreads = 200;

    private int minThreads = 5;

    private int maxIdleTime = 60 * 1000;

    private boolean preStartMinThreads = false;

    public StandardThreadExecutor() {
        this.initInternal();
    }

    private void initInternal() {
        TaskQueue queue = new TaskQueue(maxQueueSize);
        TaskThreadFactory factory = new TaskThreadFactory(namePrefix, daemon, getThreadPriority());
        executor = new ThreadPoolExecutor(getMinThreads(), getMaxThreads(), maxIdleTime, TimeUnit.MILLISECONDS, queue, factory);
        if (preStartMinThreads) {
            executor.prestartAllCoreThreads();
        }
        queue.setParent(executor);
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(Runnable command, long timeout, TimeUnit unit) {
        if (executor != null) {
            executor.execute(command, timeout, unit);
        } else {
            throw new IllegalArgumentException("ThreadPoolExecutor not started");
        }
    }

    @Override
    public void execute(Runnable command) {
        if (executor != null) {
            try {
                executor.execute(command);
            } catch (RejectedExecutionException rx) {
                if (!((TaskQueue) executor.getQueue()).force(command)) {
                    throw new RejectedExecutionException("Work queue full.");
                }
            }

        } else {
            throw new IllegalArgumentException("ThreadPoolExecutor not started");
        }
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public void setName(String name) {
        this.name = name;
    }
}

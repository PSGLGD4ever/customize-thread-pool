package com.beinglee.top.customizethreadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskThreadFactory implements ThreadFactory {

    private final String namePrefix;

    private final boolean daemon;

    private final int threadPriority;

    private final ThreadGroup group;

    private AtomicInteger threadNumber = new AtomicInteger(1);

    public TaskThreadFactory(String namePrefix, boolean daemon, int threadPriority) {
        SecurityManager s = System.getSecurityManager();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.threadPriority = threadPriority;
        this.group = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
        t.setDaemon(daemon);
        t.setPriority(threadPriority);
        // Set the context class loader of newly created threads to be the class
        // loader that loaded this factory. This avoids retaining references to
        // web application class loaders and similar.
        t.setContextClassLoader(getClass().getClassLoader());
        return t;
    }
}

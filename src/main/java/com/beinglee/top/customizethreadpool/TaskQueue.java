package com.beinglee.top.customizethreadpool;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class TaskQueue extends LinkedBlockingQueue<Runnable> {

    private volatile ThreadPoolExecutor parent;

    public TaskQueue() {
        super();
    }

    public TaskQueue(int capacity) {
        super(capacity);
    }

    public TaskQueue(Collection<Runnable> c) {
        super(c);
    }

    public void setParent(ThreadPoolExecutor parent) {
        this.parent = parent;
    }

    public boolean force(Runnable r) {
        if (parent == null || parent.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
        }
        return super.offer(r);
    }

    public boolean force(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        if (parent == null || parent.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
        }
        return super.offer(r, timeout, unit);
    }


    @Override
    public boolean offer(Runnable r) {
        if (parent == null) {
            return super.offer(r);
        }
        // 如果线程数已经到达了最大值，不能创建新线程了，只能把任务添加到队列里。
        if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
            return super.offer(r);
        }
        // 执行到这里 说明当前线程数大于核心线程数，并且小于最大线程数。
        // 表明是可以创建新的线程的，那到底创建不创建呢？这里分两种情况。

        // 1 如果已提交任务数小于当前线程数，说明有空闲线程可以处理，无需创建新线程。
        if (parent.getSubmittedCount() <= parent.getPoolSize()) {
            return super.offer(r);
        }
        // 2 如果已提交任务数大于当前线程数，说明线程不够用了，返回false，需要创建新线程。
        // 这里进行了一个常规判断，但是走到这一步的时候，已提交任务数必然是大于当前线程数。
        if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
            return false;
        }
        return super.offer(r);
    }
}

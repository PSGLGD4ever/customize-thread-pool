package com.beinglee.top.customizethreadpool;

import java.util.concurrent.TimeUnit;

public interface Executor extends java.util.concurrent.Executor {

    String getName();

    void execute(Runnable command, long timeout, TimeUnit timeUnit);
}

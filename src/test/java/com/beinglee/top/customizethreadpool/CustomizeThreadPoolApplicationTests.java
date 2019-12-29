package com.beinglee.top.customizethreadpool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = Application.class)
class CustomizeThreadPoolApplicationTests {

    private AtomicInteger threadCount = new AtomicInteger(0);


    @Test
    void contextLoads() {
    }

    @Test
    public void t1() {
        Executor executor = new StandardThreadExecutor();
        for (int i = 1; i <= 1000; i++) {
            executor.execute(create());
        }
    }

    private Runnable create() {
        return () -> System.out.println("This is The " + threadCount.getAndIncrement() + " Thread!");
    }

}

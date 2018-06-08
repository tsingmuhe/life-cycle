package com.sunchp.util.component;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class LifeCycleTest {
    @Test
    public void testStartStop() throws Exception {
        TestContainerLifeCycle a1 = new TestContainerLifeCycle();

        a1.start();
        Assert.assertEquals(1, a1.started.get());
        Assert.assertEquals(0, a1.stopped.get());

        a1.start();
        Assert.assertEquals(1, a1.started.get());
        Assert.assertEquals(0, a1.stopped.get());

        a1.stop();
        Assert.assertEquals(1, a1.started.get());
        Assert.assertEquals(1, a1.stopped.get());

        a1.start();
        Assert.assertEquals(2, a1.started.get());
        Assert.assertEquals(1, a1.stopped.get());

        a1.stop();
        Assert.assertEquals(2, a1.started.get());
        Assert.assertEquals(2, a1.stopped.get());
    }

    private static class TestContainerLifeCycle extends AbstractLifeCycle {
        private final AtomicInteger started = new AtomicInteger();
        private final AtomicInteger stopped = new AtomicInteger();

        @Override
        protected void doStart() throws Exception {
            started.incrementAndGet();
        }

        @Override
        protected void doStop() throws Exception {
            stopped.incrementAndGet();
        }
    }

}
package com.sunchp.util.component;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LifeCycleListenerTest {
    static Exception cause = new Exception("expected test exception");

    @Test
    public void testStart() throws Exception {
        TestLifeCycle lifecycle = new TestLifeCycle();
        TestListener listener = new TestListener();
        lifecycle.addLifeCycleListener(listener);
        lifecycle.setCause(cause);

        try {
            lifecycle.start();
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(cause, e);
            assertTrue("The failure event didn't occur", listener.failure);
            assertEquals(cause, listener.getCause());
        }

        lifecycle.setCause(null);
        lifecycle.start();

        assertTrue("The staring event didn't occur", listener.starting);
        assertTrue("The started event didn't occur", listener.started);
        assertTrue("The starting event must occur before the started event", listener.startingTime <= listener.startedTime);
        assertTrue("The lifecycle state is not started", lifecycle.isStarted());

        lifecycle.stop();
    }

    @Test
    public void testStop() throws Exception {
        TestLifeCycle lifecycle = new TestLifeCycle();
        TestListener listener = new TestListener();
        lifecycle.addLifeCycleListener(listener);

        lifecycle.start();
        lifecycle.setCause(cause);

        try {
            lifecycle.stop();
            assertTrue(false);
        } catch (Exception e) {
            assertEquals(cause, e);
            assertTrue("The failure event didn't occur", listener.failure);
            assertEquals(cause, listener.getCause());
        }

        lifecycle.setCause(null);
        lifecycle.stop();

        assertTrue("The stopping event didn't occur", listener.stopping);
        assertTrue("The stopped event didn't occur", listener.stopped);
        assertTrue("The stopping event must occur before the stopped event", listener.stoppingTime <= listener.stoppedTime);
        assertTrue("The lifecycle state is not stooped", lifecycle.isStopped());
    }

    @Test
    public void testRemoveLifecycleListener() throws Exception {
        TestLifeCycle lifecycle = new TestLifeCycle();
        TestListener listener = new TestListener();
        lifecycle.addLifeCycleListener(listener);

        lifecycle.start();
        assertTrue("The starting event didn't occur", listener.starting);
        lifecycle.removeLifeCycleListener(listener);
        lifecycle.stop();
        assertFalse("The stopping event occurred", listener.stopping);
    }

    private static class TestLifeCycle extends AbstractLifeCycle {
        Exception cause;

        private TestLifeCycle() {
        }

        @Override
        protected void doStart() throws Exception {
            if (cause != null)
                throw cause;
            super.doStart();
        }

        @Override
        protected void doStop() throws Exception {
            if (cause != null)
                throw cause;
            super.doStop();
        }

        public void setCause(Exception e) {
            cause = e;
        }
    }

    private class TestListener implements LifeCycle.Listener {
        private boolean failure = false;
        private boolean started = false;
        private boolean starting = false;
        private boolean stopped = false;
        private boolean stopping = false;

        private long startedTime;
        private long startingTime;
        private long stoppedTime;
        private long stoppingTime;

        private Throwable cause = null;

        public void lifeCycleFailure(LifeCycle event, Throwable cause) {
            failure = true;
            this.cause = cause;
        }

        public Throwable getCause() {
            return cause;
        }

        public void lifeCycleStarted(LifeCycle event) {
            started = true;
            startedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        }

        public void lifeCycleStarting(LifeCycle event) {
            starting = true;
            startingTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

            // need to sleep to make sure the starting and started times are not
            // the same
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void lifeCycleStopped(LifeCycle event) {
            stopped = true;
            stoppedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        }

        public void lifeCycleStopping(LifeCycle event) {
            stopping = true;
            stoppingTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

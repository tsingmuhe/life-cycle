package com.sunchp.util.component;

import java.util.EventListener;

public interface LifeCycle {
    public void start() throws Exception;

    public void stop() throws Exception;

    public boolean isRunning();

    public boolean isStarted();

    public boolean isStarting();

    public boolean isStopping();

    public boolean isStopped();

    public boolean isFailed();

    public void addLifeCycleListener(LifeCycle.Listener listener);

    public void removeLifeCycleListener(LifeCycle.Listener listener);

    public interface Listener extends EventListener {
        public void lifeCycleStarting(LifeCycle event);

        public void lifeCycleStarted(LifeCycle event);

        public void lifeCycleFailure(LifeCycle event, Throwable cause);

        public void lifeCycleStopping(LifeCycle event);

        public void lifeCycleStopped(LifeCycle event);
    }

    public static void start(Object object) {
        if (object instanceof LifeCycle) {
            try {
                ((LifeCycle) object).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void stop(Object object) {
        if (object instanceof LifeCycle) {
            try {
                ((LifeCycle) object).stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

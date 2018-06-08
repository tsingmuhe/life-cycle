package com.sunchp.util.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractLifeCycle implements LifeCycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLifeCycle.class);

    private final int FAILED = -1;
    private final int STARTING = 0;
    private final int STARTED = 1;
    private final int STOPPING = 2;
    private final int STOPPED = 3;

    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<LifeCycle.Listener>();
    private final Object lock = new Object();
    private volatile int state = STOPPED;

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception {
    }

    @Override
    public final void start() throws Exception {
        synchronized (lock) {
            try {
                if (state == STARTED || state == STARTING)
                    return;
                setStarting();
                doStart();
                setStarted();
            } catch (Throwable e) {
                setFailed(e);
                throw e;
            }
        }
    }

    @Override
    public void stop() throws Exception {
        synchronized (lock) {
            try {
                if (state == STOPPING || state == STOPPED)
                    return;
                setStopping();
                doStop();
                setStopped();
            } catch (Throwable e) {
                setFailed(e);
                throw e;
            }
        }
    }

    @Override
    public boolean isRunning() {
        return state == STARTED || state == STARTING;
    }

    @Override
    public boolean isStarted() {
        return state == STARTED;
    }

    @Override
    public boolean isStarting() {
        return state == STARTING;
    }

    @Override
    public boolean isStopping() {
        return state == STOPPING;
    }

    @Override
    public boolean isStopped() {
        return state == STOPPED;
    }

    @Override
    public boolean isFailed() {
        return state == FAILED;
    }

    @Override
    public void addLifeCycleListener(LifeCycle.Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeLifeCycleListener(LifeCycle.Listener listener) {
        listeners.remove(listener);
    }

    private void setStarting() {
        state = STARTING;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("starting {}", this);
        for (Listener listener : listeners)
            listener.lifeCycleStarting(this);
    }

    private void setStarted() {
        state = STARTED;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("started {}", this);
        for (Listener listener : listeners)
            listener.lifeCycleStarted(this);
    }

    private void setStopping() {
        state = STOPPING;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("stopping {}", this);
        for (Listener listener : listeners)
            listener.lifeCycleStopping(this);
    }

    private void setStopped() {
        state = STOPPED;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("stopped {}", this);
        for (Listener listener : listeners)
            listener.lifeCycleStopped(this);
    }

    private void setFailed(Throwable th) {
        state = FAILED;
        if (LOGGER.isDebugEnabled())
            LOGGER.warn("failed " + this + ": " + th, th);
        for (Listener listener : listeners)
            listener.lifeCycleFailure(this, th);
    }
}

package org.dronix.dmesg.task;

import android.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class BaseAsyncLoaderTask<E> extends AsyncTaskLoader<E> {
    private E mResponse;
    private AtomicBoolean mTaskStopped;

    public BaseAsyncLoaderTask(Context context) {
        super(context);
        mResponse = null;
        mTaskStopped = new AtomicBoolean(false);
    }

    @Override
    public void deliverResult(E response) {
        if (isReset()) {
            mResponse = null;
            return;
        }
        mResponse = response;
        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(mResponse);
        }

        mResponse = null;
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mResponse != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mResponse);
        } else
            forceLoad();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
        mTaskStopped.set(true);
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        mResponse = null;
    }

    @Override
    public E loadInBackground() {
        return null;
    }

    public boolean isStopped() {
        return mTaskStopped.get();
    }
}

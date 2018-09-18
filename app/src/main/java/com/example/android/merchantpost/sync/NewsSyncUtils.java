package com.example.android.merchantpost.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.merchantpost.data.NewsContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class NewsSyncUtils {

    /*
     * Interval at which to sync with the news.
     */
   /* private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;*/


    // These  settings are merely for testing purposes, the recommended settings are the one above.
    private static final int REMINDER_INTERVAL_MINUTES = 1;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    private static boolean sInitialized;

    //  Sync tag to identify our sync job
    private static final String NEWS_SYNC_TAG = "news-sync";

    /**
     * Schedules a repeating sync of news data using FirebaseJobDispatcher.
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job syncNewsJob = dispatcher.newJobBuilder()
                .setService(NewsFirebaseJobService.class)
                .setTag(NEWS_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncNewsJob);
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;
        sInitialized = true;
        scheduleFirebaseJobDispatcherSync(context);
        Thread checkForEmpty = new Thread(new Runnable() {

            @Override
            public void run() {
                Uri newsQueryUri = NewsContract.NewsEntry.CONTENT_URI;
                String[] projectionColumns = {NewsContract.NewsEntry._ID};
                String selectionStatement = NewsContract.NewsEntry.COLUMN_DATE;
                Cursor cursor = context.getContentResolver().query(
                        newsQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }
                cursor.close();
            }
        });
        checkForEmpty.start();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, NewsSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}

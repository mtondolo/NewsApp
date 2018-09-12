package com.example.android.merchantpost.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.merchantpost.data.NewsContract;

public class NewsSyncUtils {

    private static boolean sInitialized;

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {

        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;

        // If the method body is executed, set sInitialized to true
        sInitialized = true;

        /*
         * We need to check to see if our ContentProvider has data to display in our news
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                /* URI for every row of news data in our news table*/
                Uri newsQueryUri = NewsContract.NewsEntry.CONTENT_URI;

                /*
                 * Since this query is going to be used only as a check to see if we have any
                 * data (rather than to display data), we just need to PROJECT the ID of each
                 * row. In our queries where we display data, we need to PROJECT more columns
                 * to determine what weather details need to be displayed.
                 */
                String[] projectionColumns = {NewsContract.NewsEntry._ID};
                String selectionStatement = NewsContract.NewsEntry.COLUMN_DATE;

                /* Here, we perform the query to check to see if we have any news data */
                Cursor cursor = context.getContentResolver().query(
                        newsQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);

                /*
                 * If the Cursor was null OR if it was empty, we need to sync immediately to
                 * be able to display data to the user.
                 */

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                /* Close the Cursor to avoid memory leaks! */
                cursor.close();
                return null;
            }
        }.execute();

    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, NewsSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}

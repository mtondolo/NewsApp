package com.example.android.merchantpost.utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.merchantpost.NewsActivity;
import com.example.android.merchantpost.R;
import com.example.android.merchantpost.data.NewsContract;
import com.example.android.merchantpost.data.NewsPreferences;

public class NotificationUtils {

    /*
     * The columns of data that we are interested in displaying within our notification to let
     * the user know there is new news data available.
     */
    public static final String[] NEWS_NOTIFICATION_PROJECTION = {
            NewsContract.NewsEntry.COLUMN_TITLE
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able
     * to access the data from our query. If the order of the Strings above changes, these
     * indices must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_TITLE = 0;

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 3004 is in no way significant.
     */
    private static final int NEWS_NOTIFICATION_ID = 3004;

    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String NEWS_NOTIFICATION_CHANNEL_ID = "news_notification_channel";

    /**
     * Constructs and displays a notification for the newly updated news for today.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyUserOfLatestNews(Context context) {

        /* Build the URI for today's news in order to show up to date data in notification */
        Uri todaysNewsUri = NewsContract.NewsEntry
                .buildNewsUriWithDate(NewsDateUtils.normalizeDate(System.currentTimeMillis()));

        /*
         * The MAIN_NEWS_PROJECTION array passed in as the second parameter is defined in our NewsContract
         * class and is used to limit the columns returned in our cursor.
         */
        Cursor todaysNewsCursor = context.getContentResolver().query(
                todaysNewsUri,
                NEWS_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        /*
         * If todaysNewsCursor is empty, moveToFirst will return false. If our cursor is not
         * empty, we want to show the notification.
         */
        if (todaysNewsCursor.moveToFirst()) {

            String message = todaysNewsCursor.getString(INDEX_TITLE);

            String notificationText = getNotificationText(context, message);

            /*
             * NotificationCompat Builder is a very convenient way to build backward-compatible
             * notifications. In order to use it, we provide a context and specify a color for the
             * notification, a couple of different icons, the message for the notification, and
             * finally the text of the notification, which in our case in a summary of today's
             * forecast.
             */
            //NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NEWS_NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            /*
             * This Intent will be triggered when the user clicks the notification. In our case,
             * we want to open Sunshine to the DetailActivity to display the newly updated weather.
             */
            Intent newsIntentForToday = new Intent(context, NewsActivity.class);
            newsIntentForToday.setData(todaysNewsUri);

            // Use TaskStackBuilder to create the proper PendingIntent
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(newsIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the content Intent of the NotificationBuilder
            notificationBuilder.setContentIntent(resultPendingIntent);

            // Get a reference to the NotificationManager
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            /* NEWS_NOTIFICATION_ID allows you to update or cancel the notification later on */
            notificationManager.notify(NEWS_NOTIFICATION_ID, notificationBuilder.build());

            /*
             * Since we just showed a notification, save the current time. That way, we can check
             * next time the weather is refreshed if we should show another notification.
             */
            NewsPreferences.saveLastNotificationTime(context, System.currentTimeMillis());

        }

        /* Always close your cursor when you're done with it to avoid wasting resources. */
        todaysNewsCursor.close();

    }

    /**
     * Constructs and returns the summary of a particular day's forecast using various utility
     * methods and resources for formatting. This method is only used to create the text for the
     * notification that appears when the weather is refreshed.
     * <p>
     * The String returned from this method will look something like this:
     * <p>
     * Forecast: Sunny - High: 14°C Low 7°C
     *
     * @param context   Used to access utility methods and resources
     * @return Summary of a particular day's forecast
     */
    private static String getNotificationText(Context context, String message) {

        String notificationTitle = context.getString(R.string.app_name);

        String notificationFormat = context.getString(R.string.format_notification);

        /* Using String's format method, we create the news summary */
        @SuppressLint("StringFormatMatches") String notificationText = String.format(notificationFormat,
                notificationTitle,message );

        return notificationText;

    }

}

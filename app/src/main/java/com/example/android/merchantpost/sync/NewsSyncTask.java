package com.example.android.merchantpost.sync;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.format.DateUtils;

import com.example.android.merchantpost.data.NewsContract;
import com.example.android.merchantpost.data.NewsPreferences;
import com.example.android.merchantpost.utils.NewsJsonUtils;
import com.example.android.merchantpost.utils.NewsNetworkUtils;
import com.example.android.merchantpost.utils.NotificationUtils;

import java.net.URL;

public class NewsSyncTask {

    /**
     * Performs the network request for updated news, parses the JSON from that request, and
     * inserts the new news information into our ContentProvider. Will notify the user that new
     * news has been loaded if the user hasn't been notified of the news within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    synchronized public static void syncNews(Context context) {

        try {
            URL newsRequestUrl = NewsNetworkUtils.buildUrlForNews();
            String jsonNewsResponse = NewsNetworkUtils.getResponseFromHttpUrl(newsRequestUrl);
            ContentValues[] newsValues = NewsJsonUtils.getSimpleNewsStringsFromJson(context, jsonNewsResponse);
            if (newsValues != null && newsValues.length != 0) {
                ContentResolver newsContentResolver = context.getContentResolver();
                newsContentResolver.delete(
                        NewsContract.NewsEntry.CONTENT_URI,
                        null,
                        null);
                newsContentResolver.bulkInsert(
                        NewsContract.NewsEntry.CONTENT_URI,
                        newsValues);
                boolean notificationsEnabled = NewsPreferences.areNotificationsEnabled(context);

                /*
                 * If the last notification was shown was more than 1 day ago, we want to send
                 * another notification to the user that the weather has been updated. Remember,
                 * it's important that you shouldn't spam your users with notifications.
                 */
                long timeSinceLastNotification = NewsPreferences
                        .getEllapsedTimeSinceLastNotification(context);
                boolean oneDayPassedSinceLastNotification = false;
                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }
                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfLatestNews(context);
                }

                /* If the code reaches this point, we have successfully performed our sync */
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

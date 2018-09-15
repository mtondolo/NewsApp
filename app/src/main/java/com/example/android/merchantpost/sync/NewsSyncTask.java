package com.example.android.merchantpost.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.merchantpost.data.NewsContract;
import com.example.android.merchantpost.utils.NewsJsonUtils;
import com.example.android.merchantpost.utils.NewsNetworkUtils;

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
    synchronized public static void syncNews(Context context) {

        try {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * news.
             *
             */
            URL newsRequestUrl = NewsNetworkUtils.buildUrlForNews();

            /* Use the URL to retrieve the JSON */
            String jsonNewsResponse = NewsNetworkUtils.getResponseFromHttpUrl(newsRequestUrl);

            /* Parse the JSON into a list of news values */
            ContentValues[] newsValues = NewsJsonUtils.getNewsContentValuesFromJson(jsonNewsResponse);

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (newsValues != null && newsValues.length != 0) {

                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver newsContentResolver = context.getContentResolver();

                /* Delete old news data because we don't need to keep multiple days' data */
                newsContentResolver.delete(
                        NewsContract.NewsEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new news data into News's ContentProvider */
                newsContentResolver.bulkInsert(
                        NewsContract.NewsEntry.CONTENT_URI,
                        newsValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

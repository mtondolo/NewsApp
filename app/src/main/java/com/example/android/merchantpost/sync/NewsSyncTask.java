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
     * inserts the new news information into our ContentProvider.
     */
    synchronized public static void syncNews(Context context) {

        try {
            URL newsRequestUrl = NewsNetworkUtils.buildUrlForNews();
            String jsonNewsResponse = NewsNetworkUtils.getResponseFromHttpUrl(newsRequestUrl);

            /* Parse the JSON into a list of news values */
            ContentValues[] newsValues = NewsJsonUtils.getNewsContentValuesFromJson(jsonNewsResponse);

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null.
             */
            if (newsValues != null && newsValues.length != 0) {
                ContentResolver newsContentResolver = context.getContentResolver();
                newsContentResolver.delete(
                        NewsContract.NewsEntry.CONTENT_URI,
                        null,
                        null);
                newsContentResolver.bulkInsert(
                        NewsContract.NewsEntry.CONTENT_URI,
                        newsValues);

                /* If the code reaches this point, we have successfully performed our sync */
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

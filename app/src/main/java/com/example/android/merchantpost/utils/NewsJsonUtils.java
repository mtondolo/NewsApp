
package com.example.android.merchantpost.utils;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.example.android.merchantpost.data.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     *
     * @param newsJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getSimpleNewsStringsFromJson(Context context, String newsJsonStr)
            throws JSONException {

        // News item. Each news item is an element of the "articles" array
        final String NEWS_LIST = "articles";

        // News headline for the news item
        final String NEWS_TITLE = "title";

        // News published date for the news item
        final String NEWS_DATE = "publishedAt";

        // News source name for the news item
        final String NEWS_AUTHOR = "author";

        // News source url for the news item
        final String NEWS_URL = "url";

        JSONObject newsJson = new JSONObject(newsJsonStr);

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        JSONArray newsArray = newsJson.getJSONArray(NEWS_LIST);

        ContentValues[] newsContentValues = new ContentValues[newsArray.length()];

        long normalizedUtcStartDay = NewsDateUtils.getNormalizedUtcDateForToday();

        for (int i = 0; i < newsArray.length(); i++) {

            /* These are the values that will be collected */
            String title;
            String author;
            long dateTimeMillis;

            /* Get the JSON object representing the new item */
            JSONObject newsItem = newsArray.getJSONObject(i);

            title = newsItem.getString(NEWS_TITLE);
            author = newsItem.getString(NEWS_AUTHOR);
            dateTimeMillis = newsItem.getLong(NEWS_DATE);

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
            //dateTimeMillis = normalizedUtcStartDay + NewsDateUtils.DAY_IN_MILLIS;

            ContentValues newsValues = new ContentValues();

            newsValues.put(NewsContract.NewsEntry.COLUMN_TITLE, title);
            newsValues.put(NewsContract.NewsEntry.COLUMN_AUTHOR, author);
            newsValues.put(NewsContract.NewsEntry.COLUMN_DATE, dateTimeMillis);

            newsContentValues[i] = newsValues;
        }

        return newsContentValues;
    }

}


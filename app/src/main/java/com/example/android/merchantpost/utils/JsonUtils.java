
package com.example.android.merchantpost.utils;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.example.android.merchantpost.data.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

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

        // News source object for the news item
        final String NEWS_SOURCE = "source";


        // News headline for the news item
        final String NEWS_TITLE = "title";

        // News published date for the news item
        final String NEWS_DATE = "publishedAt";

        // News source url for the news item
        final String NEWS_URL = "url";

        // News source name for the news item
        final String NEWS_SOURCE_NAME = "name";

        JSONObject newsJson = new JSONObject(newsJsonStr);

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        JSONArray newsArray = newsJson.getJSONArray(NEWS_LIST);

        ContentValues[] newsContentValues = new ContentValues[newsArray.length()];

        //long normalizedUtcStartDay = NewsDateUtils.getNormalizedUtcDateForToday();

        for (int i = 0; i < newsArray.length(); i++) {

            /* These are the values that will be collected */
            String title;
            String source;
            String date;

            /* Get the JSON object representing the new item */
            JSONObject newsItem = newsArray.getJSONObject(i);
            JSONObject newsSource = newsItem.getJSONObject(NEWS_SOURCE);

            title = newsItem.getString(NEWS_TITLE);
            source = newsSource.getString(NEWS_SOURCE_NAME);
            date = newsItem.getString(NEWS_DATE);

            //dateTimeMillis = normalizedUtcStartDay + NewsDateUtils.DAY_IN_MILLIS * i;

            ContentValues newsValues = new ContentValues();
            newsValues.put(NewsContract.NewsEntry.COLUMN_DATE, date);
            newsValues.put(NewsContract.NewsEntry.COLUMN_TITLE, title);
            newsValues.put(NewsContract.NewsEntry.COLUMN_SOURCE, source);
            newsContentValues[i] = newsValues;
        }

        return newsContentValues;
    }

}


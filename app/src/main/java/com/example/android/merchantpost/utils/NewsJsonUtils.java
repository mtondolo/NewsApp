
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
     */
    public static ContentValues[] getNewsContentValuesFromJson(String newsJsonStr)
            throws JSONException {

        // News item. Each news item is an element of the "articles" array
        final String NEWS_LIST = "articles";

        // News headline, date and author of the news item
        final String NEWS_TITLE = "title";
        final String NEWS_DATE = "publishedAt";
        final String NEWS_AUTHOR = "author";

        JSONObject newsJson = new JSONObject(newsJsonStr);

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        JSONArray newsArray = newsJson.getJSONArray(NEWS_LIST);

        ContentValues[] newsContentValues = new ContentValues[newsArray.length()];

        for (int i = 0; i < newsArray.length(); i++) {

            /* These are the values that will be collected */
            String title;
            String author;
            String date;

            /* Get the JSON object representing the new item */
            JSONObject newsItem = newsArray.getJSONObject(i);

            title = newsItem.getString(NEWS_TITLE);
            author = newsItem.getString(NEWS_AUTHOR);
            date = newsItem.getString(NEWS_DATE);

            ContentValues newsValues = new ContentValues();

            newsValues.put(NewsContract.NewsEntry.COLUMN_TITLE, title);
            newsValues.put(NewsContract.NewsEntry.COLUMN_AUTHOR, author);
            newsValues.put(NewsContract.NewsEntry.COLUMN_DATE, date);

            newsContentValues[i] = newsValues;
        }

        return newsContentValues;
    }

}



package com.example.android.newsapp.utils;

import android.content.ContentValues;
import android.text.TextUtils;

import com.example.android.newsapp.data.NewsContract;

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

        // Image, headline, date, author and web page of the news item
        final String NEWS_IMAGE = "urlToImage";
        final String NEWS_TITLE = "title";
        final String NEWS_DATE = "publishedAt";
        final String NEWS_AUTHOR = "author";
        final String NEWS_URL = "url";

        JSONObject newsJson = new JSONObject(newsJsonStr);

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        JSONArray newsArray = newsJson.getJSONArray(NEWS_LIST);

        ContentValues[] newsContentValues = new ContentValues[newsArray.length()];

        for (int i = 0; i < newsArray.length(); i++) {

            /* These are the values that will be collected */
            String urlToImage;
            String title;
            String author;
            String date;
            String url;

            /* Get the JSON object representing the new item */
            JSONObject newsItem = newsArray.getJSONObject(i);

            urlToImage = newsItem.getString(NEWS_IMAGE);
            title = newsItem.getString(NEWS_TITLE);
            author = newsItem.getString(NEWS_AUTHOR);
            date = newsItem.getString(NEWS_DATE);
            url = newsItem.getString(NEWS_URL);

            ContentValues newsValues = new ContentValues();

            newsValues.put(NewsContract.NewsEntry.COLUMN_IMAGE, urlToImage);
            newsValues.put(NewsContract.NewsEntry.COLUMN_TITLE, title);
            newsValues.put(NewsContract.NewsEntry.COLUMN_AUTHOR, author);
            newsValues.put(NewsContract.NewsEntry.COLUMN_DATE, date);
            newsValues.put(NewsContract.NewsEntry.COLUMN_URL, url);

            newsContentValues[i] = newsValues;
        }

        return newsContentValues;
    }

}


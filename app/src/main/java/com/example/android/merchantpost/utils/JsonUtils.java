package com.example.android.merchantpost.utils;

import android.content.Context;
import android.text.TextUtils;

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
    public static String[] getSimpleNewsStringsFromJson(Context context, String newsJsonStr)
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
        final String NEWS_URL="url";

        // News source name for the news item
        final String NEWS_SOURCE_NAME = "name";

        // String array to hold each news item String */
        String[] parsedNewsData = null;

        JSONObject newsJson = new JSONObject(newsJsonStr);

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJsonStr)) {
            return null;
        }

        JSONArray newsArray = newsJson.getJSONArray(NEWS_LIST);

        parsedNewsData = new String[newsArray.length()];

        for (int i = 0; i < newsArray.length(); i++) {

            /* These are the values that will be collected */
            String title;
            String source;
            String date;
            String url;

            /* Get the JSON object representing the new item */
            JSONObject newsItem = newsArray.getJSONObject(i);

            // Extract the value for the key called "name"
            JSONObject newsSource = newsItem.getJSONObject(NEWS_SOURCE);

            // Extract the value for the key called "title" and "date" and "name"
            title = newsItem.getString(NEWS_TITLE);
            date = newsItem.getString(NEWS_DATE);
            url = newsItem.getString(NEWS_URL);
            source = newsSource.getString(NEWS_SOURCE_NAME);

            parsedNewsData[i] = title + " - " + date + " - " + source;
        }

        return parsedNewsData;
    }

}

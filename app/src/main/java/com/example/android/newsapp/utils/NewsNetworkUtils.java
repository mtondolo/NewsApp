package com.example.android.newsapp.utils;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NewsNetworkUtils {

    // Constant for logging
    private static final String TAG = NewsNetworkUtils.class.getSimpleName();

    final static String NEWS_API_BASE_URL =
            "https://newsapi.org/v2/top-headlines?" +
                    "sources=al-jazeera-english,bbc-news,cnn";
    final static String PARAM_KEY = "apiKey";
    final static String apiKey = "641959fcdf1e4463bbf0e95a63efced5";

    /**
     * Builds the URL used to query News API.
     */
    public static URL buildUrlForNews() {
        Uri builtUri = Uri.parse(NEWS_API_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_KEY, apiKey)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
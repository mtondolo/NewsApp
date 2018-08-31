package com.example.android.savannapost.utils;

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
public class NetworkUtils {

    // Constant for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    final static String NEWS_API_BASE_URL =
            "https://newsapi.org/v2/top-headlines";

    final static String PARAM_QUERY = "sources";
    final static String PARAM_KEY = "apiKey";

    final static String apiKey = "641959fcdf1e4463bbf0e95a63efced5";
    final static String sources = "bbc-news,cnn,al-jazeera-english,news24,google-news";

    /**
     * Builds the URL used to query News API.
     *
     * @return The URL to use to query the News API.
     */
    public static URL buildUrl() {
        Uri builtUri = Uri.parse(NEWS_API_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, sources)
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
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
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